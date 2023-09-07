package me.ionar.salhack.module;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Value<T> {

    private String name;
    private String[] alias;
    private String description;
    private Module Module;
    public ValueListeners Listener;

    private T value;

    private T min;
    private T max;
    private T increment;

    public Value(String name, String[] alias, String description) {
        this.name = name;
        this.alias = alias;
        this.description = description;
    }

    public Value(String name, String[] alias, String description, T value) {
        this(name, alias, description);
        this.value = value;
    }

    public Value(String name, String[] alias, String description, T value, T min, T max, T increment) {
        this(name, alias, description, value);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public <T> T clamp(T value, T min, T max) {
        return ((Comparable) value).compareTo(min) < 0 ? min : (((Comparable) value).compareTo(max) > 0 ? max : value);
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        if (min != null && max != null) {
            final Number val = (Number) value;
            /* final Number min = (Number) this.min;
            final Number max = (Number) this.max;*/
            this.value = (T) val;
            // this.value = (T) this.clamp(val, min, max);
        } else this.value = value;


        if (Module != null) Module.signalValueChange(this);
        if (Listener != null) Listener.OnValueChange(this);
    }

    public String GetNextEnumValue(boolean reverse) {
        final Enum currentEnum = (Enum) this.getValue();

        int i = 0;

        for (; i < this.value.getClass().getEnumConstants().length; i++) {
            final Enum e = (Enum) this.value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(currentEnum.name())) break;
        }

        return this.value.getClass().getEnumConstants()[(reverse ? (i != 0 ? i - 1 : value.getClass().getEnumConstants().length - 1) : i + 1) % value.getClass().getEnumConstants().length].toString();
    }

    public int getEnum(String input) {
        for (int i = 0; i < this.value.getClass().getEnumConstants().length; i++) {
            final Enum e = (Enum) this.value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(input)) {
                return i;
            }
        }
        return -1;
    }

    public Enum GetEnumReal(String input) {
        for (int i = 0; i < this.value.getClass().getEnumConstants().length; i++) {
            final Enum e = (Enum) this.value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(input)) {
                return e;
            }
        }
        return null;
    }

    public void setEnumValue(String value) {
        for (Enum e : ((Enum) this.value).getClass().getEnumConstants()) {
            if (e.name().equalsIgnoreCase(value)) {
                setValue((T)e);
                break;
            }
        }

        if (Module != null) Module.SignalEnumChange();
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public T getIncrement() {
        return increment;
    }

    public void setIncrement(T increment) {
        this.increment = increment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getAlias() {
        return alias;
    }

    public void setAlias(String[] alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setListener(ValueListeners listener) {
        Listener = listener;
    }

    public void InitializeModule(Module module) {
        Module = module;
    }

    public void SetForcedValue(T value) {
        if (min != null && max != null) {
            final Number val = (Number) value;
            /*final Number min = (Number) this.min;
            final Number max = (Number) this.max;*/
            this.value = (T) val;
            // this.value = (T) this.clamp(val, min, max);
        } else this.value = value;
    }
}
