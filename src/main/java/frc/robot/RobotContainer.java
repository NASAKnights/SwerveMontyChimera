// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.kauailabs.navx.frc.AHRS;

import static frc.robot.Constants.kNavXPort;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.lib.control.motors.NKTalonFX;
import frc.robot.Constants.OIConstants;
import frc.robot.drive.SwerveDrive;
import frc.robot.drive.commands.TeleopDrive;
import frc.robot.indexer.Indexer;
import frc.robot.indexer.commands.IntakeIndexCommand;
import frc.robot.indexer.commands.ShootIndexCommand;
import frc.robot.intake.Intake;
import frc.robot.intake.commands.IntakeCommand;
import frc.robot.shooter.Shooter;
import frc.robot.shooter.commands.SpinUpShooter;
import frc.robot.shooter.commands.StopShooter;

public class RobotContainer {
    private Joystick driver;
    private Joystick operator;

    private AHRS navx;
    private SwerveDrive swerve;
    private Shooter shooter;
    private Indexer indexer;
    private Intake intake;


  public RobotContainer() {

    driver = new Joystick(Constants.kDriverPort);
    operator = new Joystick(Constants.kOperatorPort);

    navx = new AHRS(kNavXPort);

    swerve = new SwerveDrive(navx);
    
    swerve.readoffsets();
    swerve.initDashboard();
    swerve.updateSmartDash();
    // swerve.updateOffsets();

    shooter = new Shooter();
    intake = new Intake();
    indexer = new Indexer();

    configureDefaultCommands();
    configureButtonBindings();
  }

  private void configureDefaultCommands(){
    swerve.setDefaultCommand(new TeleopDrive(driver, swerve));

    intake.setDefaultCommand(new IntakeCommand(intake, indexer, operator));
    indexer.setDefaultCommand(new IntakeIndexCommand(intake, indexer));
  }
  
  private void configureButtonBindings() {
    new JoystickButton(driver, OIConstants.kSquare).onTrue(new InstantCommand(swerve::resetHeading));


    if (!Constants.kDemoMode) {
      createShootingButton(operator, OIConstants.kSquare, 3650);
      createShootingButton(operator, OIConstants.kCircle, 4000);
      createShootingButton(operator, OIConstants.kX, 4500);
      createShootingButton(operator, OIConstants.kTriangle, 1500);
    }
    else {
      createShootingButton(operator, OIConstants.kSquare, 1000);
      createShootingButton(operator, OIConstants.kCircle, 2000);
      createShootingButton(operator, OIConstants.kX, 1500);
      createShootingButton(operator, OIConstants.kTriangle, 2500);
      createShootingButton(operator, OIConstants.kShare, 3650);
      createShootingButton(operator, OIConstants.kOptions, 4000);
    }

    // new JoystickButton(operator, OIConstants.kSquare)
    //     .onTrue(new SpinUpShooter(shooter, 3650, true))
    //     .whileTrue(new ShootIndexCommand(indexer, shooter))
    //     .onFalse(new StopShooter(shooter));

    // new JoystickButton(operator, OIConstants.kCircle)
    //     .onTrue(new SpinUpShooter(shooter, 4000, false)) //was 4300
    //     .whileTrue(new ShootIndexCommand(indexer, shooter))
    //     .onFalse(new StopShooter(shooter));

    // new JoystickButton(operator, OIConstants.kX)
    //     .onTrue(new SpinUpShooter(shooter, 4500, false))
    //     .whileTrue(new ShootIndexCommand(indexer, shooter))
    //     .onFalse(new StopShooter(shooter));

    // new JoystickButton(operator, OIConstants.kTriangle)
    //     .onTrue(new SpinUpShooter(shooter, 2000, false))
    //     .whileTrue(new ShootIndexCommand(indexer, shooter))
    //     .onFalse(new StopShooter(shooter));

  }

  private JoystickButton createShootingButton(Joystick controller, int buttonNumber, double rpm) {
    JoystickButton button = new JoystickButton(controller, buttonNumber);
    button.onTrue(new SpinUpShooter(shooter, rpm, false));
    button.whileTrue(new ShootIndexCommand(indexer, shooter));
    button.onFalse(new StopShooter(shooter));
    return button;
  }

  public void periodic(){
    swerve.updateSmartDash();
    // swerve.writeOffsets();
    // swerve.readoffsets();
       
  }

  public void disabledPeriodic(){
    swerve.updateSmartDash();
    swerve.writeOffsets();
    swerve.readoffsets();
    swerve.updateOffsets();
  }
  
  public Command getAutonomousCommand() {
    return new InstantCommand();
  }
}
