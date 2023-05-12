package frc.robot.indexer.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.indexer.Indexer;
import frc.robot.shooter.Shooter;

public class ShootIndexCommand extends CommandBase {
    Shooter shooter;
    Indexer indexer;

    public ShootIndexCommand(Indexer indexer, Shooter shooter) {
        this.shooter = shooter;
        this.indexer = indexer;
        addRequirements(indexer);
    }

    @Override
    public void execute() {
        if (shooter.isAtTarget()) indexer.shootIndex();
        else indexer.stopIndex();
    }

    @Override
    public void end(boolean interrupted) {
        indexer.stopIndex();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}