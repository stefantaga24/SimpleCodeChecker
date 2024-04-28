package cs108;

import java.util.List;
import java.util.function.IntBinaryOperator;

public final class Cell extends AbstractSubject implements Observer{
    private static final String COLUMN_NAME = "ABCDEFGHI";
    private final int column, row;

    private String contentString;
    private List<Cell> arguments;
    private IntBinaryOperator operator;
    private int value;

    public Cell(int column, int row, int initialValue) {
        this.column = column;
        this.row = row;
        this.contentString = String.valueOf(initialValue);
        this.arguments = List.of();
        this.operator = (x, y) -> initialValue;
        this.value = initialValue;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public String getName() {
        return String.valueOf(COLUMN_NAME.charAt(column)) + (row + 1);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int newValue) {
        if (newValue != value)
        {
            value = newValue;
            System.out.println(this.column+" "+this.row);
            this.subjectChanged();
        }
    }

    public String getFormulaString() {
        return contentString;
    }

    public void setFormula(String newContentString,
                           List<Cell> newArguments,
                           IntBinaryOperator newOperator) {
        contentString = newContentString;
        arguments.forEach(hello -> {hello.removeObserver(this);});
        arguments = newArguments;
        arguments.forEach(hello->{hello.addObserver(this);});
        arguments.forEach(System.out::println);
        operator = newOperator;

        updateContent();
        this.subjectChanged();
    }

    private void updateContent() {
        int arg0 = arguments.size() > 0 ? arguments.get(0).getValue() : 0;
        int arg1 = arguments.size() > 1 ? arguments.get(1).getValue() : 0;
        for (int i=0;i<3;i++)
        {
            for (int j=0;j<3;j++)
            {
                for (int k=0;k<4;k++)
                {

                }
            }
        }
        setValue(operator.applyAsInt(arg0, arg1));
    }

    @Override
    public void update(Subject x) {
        updateContent();
    }
}
