package interpreting.datatypes;

import interpreting.RuntimeResult;
import lexing.Position;

import java.util.List;

public class NumLiteral implements Value {

    private int intValue;
    private double floatValue;
    private Position posBegin;
    private Position posEnd;

    public NumLiteral(int value) {
        this.intValue = value;
        this.posBegin = null;
        this.posEnd = null;
    }

    public NumLiteral(double value) {
        this.floatValue = value;
        this.posBegin = null;
        this.posEnd = null;
    }

    public RuntimeResult execute(List<Value> args, CoreFunction closure) {
        return null;
    }
    public void setName(String name) {}

    public NumLiteral setPosition(Position posBegin, Position posEnd) {
        if (posBegin != null) this.posBegin = posBegin;
        if (posEnd != null) this.posEnd = posEnd;

        return this;
    }

    public double getDoubleValue() { return floatValue; }
    public int getIntegerValue() {
        return intValue;
    }
    public Position getPosEnd() {
        return null;
    }
    public Position getPosBegin() {
        return null;
    }
    public Object getIntValue() { return null; }
    public String toString() {
        return Integer.toString(intValue);
    }

    public NumLiteral add(NumLiteral other) {
        double thisRealValue = this.intValue + this.floatValue;
        double otherRealValue = other.getIntegerValue() + other.getDoubleValue();
        double sumValue = thisRealValue + otherRealValue;
        NumLiteral sum;
        if (Double.toString(sumValue).endsWith(".0")) {
            sum = new NumLiteral((int) (sumValue));
        } else {
            sum = new NumLiteral(sumValue);
        }
        return sum;
    }

    public NumLiteral subtract(NumLiteral other) {
        double thisRealValue = this.intValue + this.floatValue;
        double otherRealValue = other.getIntegerValue() + other.getDoubleValue();
        double differenceValue = thisRealValue - otherRealValue;
        NumLiteral difference;
        if (Double.toString(differenceValue).endsWith(".0")) {
            difference = new NumLiteral((int) (differenceValue));
        } else {
            difference = new NumLiteral(differenceValue);
        }
        return difference;
    }

    public NumLiteral multiply(NumLiteral other) {
        double thisRealValue = this.intValue + this.floatValue;
        double otherRealValue = other.getIntegerValue() + other.getDoubleValue();
        double productValue = thisRealValue * otherRealValue;
        NumLiteral product;
        if (Double.toString(productValue).endsWith(".0")) {
            product = new NumLiteral((int) (productValue));
        } else {
            product = new NumLiteral(productValue);
        }
        return product;
    }

    public NumLiteral divide(NumLiteral other) {
        double thisRealValue = this.intValue + this.floatValue;
        double otherRealValue = other.getIntegerValue() + other.getDoubleValue();
        double quotientValue = thisRealValue / otherRealValue;
        NumLiteral quotient;
        if (otherRealValue == 0) throw new RuntimeException("Please do not divide by zero.");

        if (Double.toString(quotientValue).endsWith(".0")) {
            quotient = new NumLiteral((int) (quotientValue));
        } else {
            quotient = new NumLiteral(quotientValue);
        }
        return quotient;
    }

    public NumLiteral raiseToPower(NumLiteral other) {
        double thisRealValue = this.intValue + this.floatValue;
        double otherRealValue = other.getIntegerValue() + other.getDoubleValue();
        double factorValue = Math.pow(thisRealValue, otherRealValue);
        NumLiteral factor;
        if (Double.toString(factorValue).endsWith(".0")) {
            factor = new NumLiteral((int) (factorValue));
        } else {
            factor = new NumLiteral(factorValue);
        }
        return factor;
    }

    public NumLiteral isEqualTo(NumLiteral other) {
        boolean comparisonResult =
                ((this.intValue + this.floatValue) == (other.getIntegerValue() + other.getDoubleValue()));
        if (comparisonResult) {
            return new NumLiteral(1);
        } else {
            return new NumLiteral(0);
        }
    }

    public NumLiteral isNotEqualTo(NumLiteral other) {
        boolean comparisonResult =
                ((this.intValue + this.floatValue) != (other.getIntegerValue() + other.getDoubleValue()));
        if (comparisonResult) {
            return new NumLiteral(1);
        } else {
            return new NumLiteral(0);
        }
    }

    public NumLiteral isLessThan(NumLiteral other) {
        boolean comparisonResult =
                ((this.intValue + this.floatValue) < (other.getIntegerValue() + other.getDoubleValue()));
        if (comparisonResult) {
            return new NumLiteral(1);
        } else {
            return new NumLiteral(0);
        }
    }

    public NumLiteral isGreaterThan(NumLiteral other) {
        boolean comparisonResult =
                ((this.intValue + this.floatValue) > (other.getIntegerValue() + other.getDoubleValue()));
        if (comparisonResult) {
            return new NumLiteral(1);
        } else {
            return new NumLiteral(0);
        }
    }

    public NumLiteral isLessOrEqualTo(NumLiteral other) {
        boolean comparisonResult =
                ((this.intValue + this.floatValue) <= (other.getIntegerValue() + other.getDoubleValue()));
        if (comparisonResult) {
            return new NumLiteral(1);
        } else {
            return new NumLiteral(0);
        }
    }

    public NumLiteral isGreaterOrEqualTo(NumLiteral other) {
        boolean comparisonResult =
                ((this.intValue + this.floatValue) >= (other.getIntegerValue() + other.getDoubleValue()));
        if (comparisonResult) {
            return new NumLiteral(1);
        } else {
            return new NumLiteral(0);
        }
    }

    public NumLiteral applyModulo(NumLiteral other) {
        return new NumLiteral(this.intValue % other.getIntegerValue());
    }

    public NumLiteral applyAND(NumLiteral other) {
        boolean instance = (this.intValue + this.floatValue) != 0;
        boolean argument = (other.getIntegerValue() + other.getDoubleValue()) != 0;

        if (instance && argument) {
            return new NumLiteral(1);
        } else {
            return new NumLiteral(0);
        }
    }

    public NumLiteral applyOR(NumLiteral other) {
        boolean instance = (this.intValue + this.floatValue) != 0;
        boolean argument = (other.getIntegerValue() + other.getDoubleValue()) != 0;

        if (instance || argument) {
            return new NumLiteral(1);
        } else {
            return new NumLiteral(0);
        }
    }

    public NumLiteral applyNOT() {
        boolean instance = (this.intValue + this.floatValue) == 0;
        if (instance) {
            return new NumLiteral(1);
        } else {
            return new NumLiteral(0);
        }
    }

    public boolean isTrue() {
        return (getDoubleValue() + getIntegerValue()) == 1;
    }

    public NumLiteral applyLogarithmBase2() {
        double logValue = Math.log(this.intValue + this.floatValue) / Math.log(2);
        NumLiteral log;
        if (Double.toString(logValue).endsWith(".0")) {
            log = new NumLiteral((int) (logValue));
        } else {
            log = new NumLiteral(logValue);
        }
        return log;
    }

    public NumLiteral applySquareRoot() {
        double rootValue = Math.sqrt(this.intValue + this.floatValue);
        NumLiteral root;
        if (Double.toString(rootValue).endsWith(".0")) {
            root = new NumLiteral((int) (rootValue));
        } else {
            root = new NumLiteral(rootValue);
        }
        return root;
    }

    public NumLiteral minimumOf(NumLiteral other) {
        double thisRealValue = this.intValue + this.floatValue;
        double otherRealValue = other.getIntegerValue() + other.getDoubleValue();
        double minimumValue = Math.min(thisRealValue, otherRealValue);
        NumLiteral minimum;
        if (Double.toString(minimumValue).endsWith(".0")) {
            minimum = new NumLiteral((int) (minimumValue));
        } else {
            minimum = new NumLiteral(minimumValue);
        }
        return minimum;
    }

    public NumLiteral maximumOf(NumLiteral other) {
        double thisRealValue = this.intValue + this.floatValue;
        double otherRealValue = other.getIntegerValue() + other.getDoubleValue();
        double maximumValue = Math.max(thisRealValue, otherRealValue);
        NumLiteral maximum;
        if (Double.toString(maximumValue).endsWith(".0")) {
            maximum = new NumLiteral((int) (maximumValue));
        } else {
            maximum = new NumLiteral(maximumValue);
        }
        return maximum;
    }

    public NumLiteral absoluteValue() {
        double absoluteValue = Math.abs(this.intValue + this.floatValue);
        NumLiteral result;
        if (Double.toString(absoluteValue).endsWith(".0")) {
            result = new NumLiteral((int) (absoluteValue));
        } else {
            result = new NumLiteral(absoluteValue);
        }
        return result;
    }
}
