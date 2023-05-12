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

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
    private Joystick driver;
    private Joystick operator;

    private AHRS navx;
    private SwerveDrive swerve;
    private Shooter shooter;
    private Indexer indexer;
    private Intake intake;


  /** The container for the robot. Contains subsystems, OI devices, and commands. */
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

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */

  private void configureDefaultCommands(){
    swerve.setDefaultCommand(new Drive(driver, swerve));

    intake.setDefaultCommand(new IntakeCommand(intake, indexer, operator));
    indexer.setDefaultCommand(new IntakeIndexCommand(intake, indexer));
  }
  
  private void configureButtonBindings() {
    new JoystickButton(driver, 1).onTrue(new InstantCommand(swerve::resetHeading));

    new JoystickButton(operator, OIConstants.kSquare)
        // .onTrue(new SpinUpShooter(shooter, 3650, true))
        .whileTrue(new ShootIndexCommand(indexer, shooter))
        .onFalse(new StopShooter(shooter));

    // new JoystickButton(operator, OIConstants.kCircle)
    //     .onTrue(new SpinUpShooter(shooter, 4000, false)) //was 4300
    //     .whileTrue(new ShootIndexCommand(indexer, shooter))
    //     .onFalse(new StopShooter(shooter));

    // new JoystickButton(operator, OIConstants.kX)
    //     .onTrue(new SpinUpShooter(shooter, 4500, false))
    //     .whileTrue(new ShootIndexCommand(indexer, shooter))
    //     .onFalse(new StopShooter(shooter));

    new JoystickButton(operator, OIConstants.kTriangle)
        .onTrue(new InstantCommand(() -> {shooter.flywheel.set(0.35);}));

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
  

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return new InstantCommand();
  }
}
