package edu.kis.powp.jobs2d.command;

import java.util.ArrayList;
import java.util.List;

public class FactoryCommand {

    public static DriverCommand createHouse() {
        int baseX = -50;
        int baseY = -50;
        int squareSize = 100;
        int roofHeight = 60;



        CompoundCommand square = createSquare(baseX, baseY, squareSize);

        CompoundCommand roof = createIsoscelesTriangle(baseX, baseY, squareSize, roofHeight);

        return CompoundCommand.builder()
                .add(square)
                .add(roof)
                .setName("House command")
                .build();
            
    }

    public static CompoundCommand createSquare(int baseX, int baseY, int squareSize){
        return CompoundCommand.builder()
        .addSetPosition(baseX,baseY)
        .addOperateTo(baseX + squareSize, baseY)
        .addOperateTo(baseX + squareSize, baseY + squareSize)
        .addOperateTo(baseX, baseY + squareSize)
        .addOperateTo(baseX, baseY)
        .build();
    }

    public static CompoundCommand createIsoscelesTriangle(int baseX, int baseY, int squareSize, int roofHeight){
        List<DriverCommand> commands = new ArrayList<>();
        commands.add(new SetPositionCommand(baseX, -(baseY + squareSize)));
        commands.add(new OperateToCommand(baseX + squareSize / 2, -(baseY + squareSize + roofHeight)));
        commands.add(new OperateToCommand(baseX + squareSize, -(baseY + squareSize)));

        return CompoundCommand.builder()
        .add(commands)
        .addSetPosition(baseX, -(baseY + squareSize))
        .addOperateTo(baseX + squareSize / 2, -(baseY + squareSize + roofHeight))
        .addOperateTo(baseX + squareSize, -(baseY + squareSize))
        .build();
    }


    public static DriverCommand createSecretCommand(){

        return CompoundCommand.builder()
            .addSetPosition(-20, -50)
            .addOperateTo(-20, -50)
            .addSetPosition(-20, -40)
            .addOperateTo(-20, 50)
            .addSetPosition(0, -50)
            .addOperateTo(0, -50)
            .addSetPosition(0, -40)
            .addOperateTo(0, 50)
            .addSetPosition(70, -50)
            .addOperateTo(20, -50)
            .addOperateTo(20, 0)
            .addOperateTo(70, 0)
            .addOperateTo(70, 50)
            .addOperateTo(20, 50)

            .setName("Top Secret Command")
            .build();
    }
}
