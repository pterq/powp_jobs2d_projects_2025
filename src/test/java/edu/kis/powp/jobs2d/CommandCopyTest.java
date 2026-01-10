package edu.kis.powp.jobs2d;

import edu.kis.powp.jobs2d.command.*;

import java.util.ArrayList;
import java.util.List;

public class CommandCopyTest {

    public static void main(String[] args) {

        testDriverCommandCommandCopy();
        testCompundCommandExecutionIsTheSame();
        testCompoundCommandCopy();
        testObjectIndependence();

        System.out.println("Tests passed");
    }

    static void testDriverCommandCommandCopy() {
        DriverCommand cmd = new SetPositionCommand(10, 20);
        DriverCommand copy = cmd.copy();

        assert cmd != copy : "Simple command copy is same reference";
        assert cmd.getClass() == copy.getClass() : "Simple command copy has different class";
    }

    static void testCompundCommandExecutionIsTheSame() {
        CompoundCommand original = CompoundCommand.builder()
                .addSetPosition(1, 2)
                .addOperateTo(5, 6)
                .build();

        ICompoundCommand copy = original.copy();

        MockDriver d1 = new MockDriver();
        MockDriver d2 = new MockDriver();

        original.execute(d1);
        copy.execute(d2);

        assert d1.x == d2.x : "Copy execution differs in x";
        assert d1.y == d2.y : "Copy execution differs in y";
    }

    static void testCompoundCommandCopy() {
        CompoundCommand original = CompoundCommand.builder()
                .addSetPosition(0, 0)
                .addOperateTo(10, 10)
                .build();

        ICompoundCommand copy = original.copy();

        assert original != copy : "CompoundCommand copy is the same reference";
        assert original.size() == ((CompoundCommand) copy).size()
                : "CompoundCommand copy has different size";

        java.util.Iterator<DriverCommand> it1 = original.iterator();
        java.util.Iterator<DriverCommand> it2 = copy.iterator();

        while (it1.hasNext()) {
            DriverCommand c1 = it1.next();
            DriverCommand c2 = it2.next();

            assert c1 != c2 : "Inner command was not copied";
            assert c1.getClass() == c2.getClass()
                    : "Inner command type differs after copy";
        }
    }

    static void testObjectIndependence() {
        CompoundCommand original = CompoundCommand.builder()
                .addSetPosition(1, 1)
                .build();

        ICompoundCommand copy = original.copy();

        List<DriverCommand> commands = new ArrayList<>();
        commands.add(new OperateToCommand(2, 2));
        CompoundCommand modified = original.concatCommands(commands);

        assert ((CompoundCommand) copy).size() == 1
                : "Copy affected by original modification";
        assert modified.size() == 2
                : "Modified original has wrong size";
    }
}
