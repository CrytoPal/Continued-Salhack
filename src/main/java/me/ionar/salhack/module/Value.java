package me.ionar.salhack.module;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Value<T> {

    private String Name;
    private String[] Alias;
    private String Description;
    private Module Module;
    public ValueListeners Listener;

    private T Value;

    private T Min;
    private T Max;
    private T Increment;

    public Value(String name, String[] alias, String description) {
        Name = name;
        Alias = alias;
        Description = description;
    }

    public Value(String name, String[] alias, String description, T value) {
        this(name, alias, description);
        Value = value;
    }

    public Value(String name, String[] alias, String description, T value, T min, T max, T increment) {
        this(name, alias, description, value);
        Min = min;
        Max = max;
        this.Increment = increment;
    }

    public <T> T clamp(T value, T min, T max) {
        return ((Comparable) value).compareTo(min) < 0 ? min : (((Comparable) value).compareTo(max) > 0 ? max : value);
    }

    public T getValue() {
        return Value;
    }

    public void setValue(T value) {
        Value = value;
        if (Module != null) Module.signalValueChange(this);
        if (Listener != null) Listener.OnValueChange(this);
    }

    public String GetNextEnumValue(boolean reverse) {
        final Enum currentEnum = (Enum) this.getValue();

        int i = 0;

        for (; i < Value.getClass().getEnumConstants().length; i++) {
            final Enum e = (Enum) Value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(currentEnum.name())) break;
        }

        return Value.getClass().getEnumConstants()[(reverse ? (i != 0 ? i - 1 : Value.getClass().getEnumConstants().length - 1) : i + 1) % Value.getClass().getEnumConstants().length].toString();
    }

    public int getEnum(String input) {
        for (int i = 0; i < Value.getClass().getEnumConstants().length; i++) {
            final Enum e = (Enum) Value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(input)) {
                return i;
            }
        }
        return -1;
    }

    public Enum GetEnumReal(String input) {
        for (int i = 0; i < Value.getClass().getEnumConstants().length; i++) {
            final Enum e = (Enum) Value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(input)) {
                return e;
            }
        }
        return null;
    }

    public void setEnumValue(String value) {
        for (Enum e : ((Enum) Value).getClass().getEnumConstants()) {
            if (e.name().equalsIgnoreCase(value)) {
                setValue((T)e);
                break;
            }
        }

        if (Module != null) Module.SignalEnumChange();
    }

    public T getMin() {
        return Min;
    }

    public void setMin(T min) {
        Min = min;
    }

    public T getMax() {
        return Max;
    }

    public void setMax(T max) {
        Max = max;
    }

    public T getIncrement() {
        return Increment;
    }

    public void setIncrement(T increment) {
        this.Increment = increment;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String[] getAlias() {
        return Alias;
    }

    public void setAlias(String[] alias) {
        Alias = alias;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setListener(ValueListeners listener) {
        Listener = listener;
    }

    public void InitializeModule(Module module) {
        Module = module;
    }

    public void SetForcedValue(T value) {
        Value = value;
    }
}
