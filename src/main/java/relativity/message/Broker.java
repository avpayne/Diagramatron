package relativity.message;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Broker {

	Map<Type, Set<Subscriber>> subscriberMap = new EnumMap<>(Type.class);

	public void subscribe(Type type, Subscriber subscriber) {
		subscriberMap.computeIfAbsent(type, k -> new HashSet<>()).add(subscriber);
	}

	public void unsubscribe(Subscriber subscriber) {
		for (Set<Subscriber> set : subscriberMap.values())
			if (set.contains(subscriber))
				set.remove(subscriber);
	}

	public void unsubscribe(Class<? extends Subscriber> clazz) {
		for (Set<Subscriber> set : subscriberMap.values()) {
			Set<Subscriber> setToRemove = new HashSet<>();
			for (Subscriber subscriber : set)
				if (subscriber.getClass() == clazz)
					setToRemove.add(subscriber);
			set.removeAll(setToRemove);
		}

	}

	public void publish(Message message) {
		for (Subscriber subscriber : new HashSet<>(subscriberMap.computeIfAbsent(message.type(), k -> new HashSet<>())))
			subscriber.handleMessage(message);
		for (Subscriber subscriber : new HashSet<>(subscriberMap.computeIfAbsent(Type.ALL, k -> new HashSet<>())))
			subscriber.handleMessage(message);
	}

}
