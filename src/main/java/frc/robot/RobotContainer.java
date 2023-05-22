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
import frc.robot.drive.commands.Drive;
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
    swerve.setDefaultCommand(new Drive(driver, swerve));

    intake.setDefaultCommand(new IntakeCommand(intake, indexer, operator));
    indexer.setDefaultCommand(new IntakeIndexCommand(intake, indexer));
  }
  
  private void configureButtonBindings() {
    new JoystickButton(driver, 1).onTrue(new InstantCommand(swerve::resetHeading));

    new JoystickButton(operator, OIConstants.kSquare)
        .onTrue(new SpinUpShooter(shooter, 3650, true))
        .whileTrue(new ShootIndexCommand(indexer, shooter))
        .onFalse(new StopShooter(shooter));

    new JoystickButton(operator, OIConstants.kCircle)
        .onTrue(new SpinUpShooter(shooter, 4000, false)) //was 4300
        .whileTrue(new ShootIndexCommand(indexer, shooter))
        .onFalse(new StopShooter(shooter));

    new JoystickButton(operator, OIConstants.kX)
        .onTrue(new SpinUpShooter(shooter, 4500, false))
        .whileTrue(new ShootIndexCommand(indexer, shooter))
        .onFalse(new StopShooter(shooter));

    // new JoystickButton(operator, OIConstants.kTriangle)
    //     .onTrue(new InstantCommand(() -> {shooter.flywheel.set(0.75);}));

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
