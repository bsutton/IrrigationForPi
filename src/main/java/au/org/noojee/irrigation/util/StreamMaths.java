package au.org.noojee.irrigation.util;

import java.time.Duration;
import java.util.function.Function;
import java.util.stream.Stream;


public interface StreamMaths
{

	public static <E> Duration sum(Stream<E> stream, Function<E, Duration> functor)
	{
		return stream.map(functor)
				.reduce(Duration.ZERO, (a, b) -> a.plus(b));
	}


}
