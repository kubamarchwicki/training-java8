package com.example.foo.java8;

import com.example.foo.java8.people.Person;
import com.example.foo.java8.people.PersonDao;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summarizingInt;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.data.Offset.offset;

/**
 * Explore different Collectors.* implementations
 * - Various collectors, grouping, average, toList, etc.
 */
@Ignore
public class J06_CollectorsTest {

	private final PersonDao dao = new PersonDao();

	@Test
	public void calculateAverageHeight() throws IOException {
		final List<Person> people = dao.loadPeopleDatabase();

		final Double averageHeight = people.stream()
				.mapToInt(Person::getHeight)
				.average().getAsDouble();

		assertThat(averageHeight).isEqualTo(174, offset(0.5));
	}

	@Test
	public void partitionByPeopleAboveAndBelow180CmHeight() throws IOException {
		final List<Person> people = dao.loadPeopleDatabase();

		final Map<Boolean, List<Person>> peopleByHeight = people
				.stream()
				.collect(Collectors.partitioningBy(p -> p.getHeight() > 180));

		final List<Person> tallPeople = peopleByHeight.get(true);
		assertThat(tallPeople).hasSize(33);

		final List<Person> shortPeople = peopleByHeight.get(false);
		assertThat(shortPeople).hasSize(104);
	}

	@Test
	public void groupPeopleByWeight() throws IOException {
		final List<Person> people = dao.loadPeopleDatabase();

		final Map<Integer, List<Person>> peopleByWeight = people
				.stream()
				.collect(groupingBy(Person::getWeight));

		assertThat(peopleByWeight.get(46)).hasSize(1);
		assertThat(peopleByWeight.get(70)).hasSize(2);
		assertThat(peopleByWeight.get(92)).hasSize(1);
	}

	@Test
	public void weightStatistics() throws IOException {
		final List<Person> people = dao.loadPeopleDatabase();

		final IntSummaryStatistics stats = people
				.stream()
				.collect(summarizingInt(Person::getWeight));

		assertThat(stats.getCount()).isEqualTo(137);
		assertThat(stats.getMin()).isEqualTo(46);
		assertThat(stats.getMax()).isEqualTo(92);
		assertThat(stats.getSum()).isEqualTo(8998);
		assertThat(stats.getAverage()).isEqualTo(65.8, offset(0.5));
	}

}
