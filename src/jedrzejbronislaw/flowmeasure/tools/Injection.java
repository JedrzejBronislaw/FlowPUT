package jedrzejbronislaw.flowmeasure.tools;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Injection {

	public static void run(Runnable action) {
		if(action != null)
			action.run();
	}
	
	public static <ArgType> void run(Consumer<ArgType> action, ArgType arg) {
		if(action != null)
			action.accept(arg);
	}
	
	public static <ArgType1, ArgType2> void run(BiConsumer<ArgType1, ArgType2> action, ArgType1 arg1, ArgType2 arg2) {
		if(action != null)
			action.accept(arg1, arg2);
	}

	public static <ArgType> ArgType get(Supplier<ArgType> supplier) {
		return get(supplier, null);
	}

	public static <ArgType> ArgType get(Supplier<ArgType> supplier, ArgType defaultValue) {
		return (supplier == null) ? defaultValue : supplier.get();
	}

	public static <ArgType, RetrunType> RetrunType get(Function<ArgType, RetrunType> function, ArgType arg) {
		return get(function, arg, null);
	}

	public static <ArgType, RetrunType> RetrunType get(Function<ArgType, RetrunType> function, ArgType arg, RetrunType defaultValue) {
		return (function == null) ? defaultValue : function.apply(arg);
	}
}
