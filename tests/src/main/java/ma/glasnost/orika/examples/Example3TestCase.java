/*
 * Orika - simpler, better and faster Java bean mapping
 *
 * Copyright (C) 2011-2013 Orika authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ma.glasnost.orika.examples;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stanislav.i.petrov@gmail.com
 */
public class Example3TestCase {

    @Test
    public void nestedElements() {

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("names{fullName}", "personalNames{key}");
        fieldMap.put("names{}", "personalNames{value}");
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().mapNulls(false).dumpStateOnException(false).build();
        ClassMapBuilder<Person, PersonDto> classBuilder = mapperFactory.classMap(Person.class, PersonDto.class);
        fieldMap.forEach(classBuilder::field);
        classBuilder.register();

        BoundMapperFacade<Person, PersonDto> delegate = mapperFactory.getMapperFacade(Person.class, PersonDto.class);

        Person person = new Person();
        Name n1 = new Name("raj", "kumar", "raj kumar");
        Name n2 = new Name("senthil", "kumar", "senthil kumar");
        person.setNames(Arrays.asList(n1, n2));
        PersonDto pDto = delegate.map(person);
        System.out.println(pDto);

        Assert.assertNotNull(pDto);
    }

    @Test
    public void fixIssue() {

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("names{fullName}", "personalNames{key}");
        fieldMap.put("names{}", "personalNames{value}");
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().mapNulls(false).dumpStateOnException(false).build();
        ClassMapBuilder<PersonDto, Person> classBuilder = mapperFactory.classMap(PersonDto.class, Person.class);
        fieldMap.forEach((k, v) -> classBuilder.field(v, k));
        classBuilder.register();

        BoundMapperFacade<Person, PersonDto> delegate = mapperFactory.getMapperFacade(Person.class, PersonDto.class);

        Person person = new Person();
        Name n1 = new Name("raj", "kumar", "raj kumar");
        Name n2 = new Name("senthil", "kumar", "senthil kumar");
        person.setNames(Arrays.asList(n1, n2));
        PersonDto pDto = delegate.map(person);

        System.out.println(pDto);
        Assert.assertNotNull(pDto);
    }

    @Test
    public void nestedElementsBtoA() {

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("names{fullName}", "personalNames{key}");
        fieldMap.put("names{}", "personalNames{value}");
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().mapNulls(false).dumpStateOnException(false).build();
        ClassMapBuilder<PersonDto, Person> classBuilder = mapperFactory.classMap(PersonDto.class, Person.class);
        fieldMap.forEach((k, v) -> classBuilder.field(v, k));
        classBuilder.register();

        BoundMapperFacade<PersonDto, Person> delegate = mapperFactory.getMapperFacade(PersonDto.class, Person.class);

        PersonDto pDto = new PersonDto();
        Map<String, Name> names = new HashMap<>();
        pDto.setPersonalNames(names);
        Name n1 = new Name("raj", "kumar", "raj kumar");
        Name n2 = new Name("senthil", "kumar", "senthil kumar");
        names.put(n1.getFullName(), n1);
        names.put(n2.getFullName(), n2);

        Person pSrc = delegate.map(pDto);

        System.out.println("----- src: " + pSrc);
        Assert.assertNotNull(pSrc);

        Person person = new Person();
        person.setNames(Arrays.asList(n1, n2));

        BoundMapperFacade<Person, PersonDto> delegate1 = mapperFactory.getMapperFacade(Person.class, PersonDto.class);
        PersonDto dest = delegate1.map(person);
        System.out.println("----- dest: " + dest);
        Assert.assertNotNull(dest);
    }

    public static class Name {
        private String first;
        private String last;
        private String fullName;
        // getters/setters

        public Name() {

        }

        public Name(String first, String last, String fullName) {
            this.first = first;
            this.last = last;
            this.fullName = fullName;
        }

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Name{");
            sb.append("first='").append(first).append('\'');
            sb.append(", last='").append(last).append('\'');
            sb.append(", fullName='").append(fullName).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public static class Person {
        private List<Name> names;
        // getters/setters


        public List<Name> getNames() {
            return names;
        }

        public void setNames(List<Name> names) {
            this.names = names;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Person{");
            sb.append("names=").append(names);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class PersonDto {
        private Map<String, Name> personalNames;

        public PersonDto() {
        }

        public Map<String, Name> getPersonalNames() {
            return personalNames;
        }

        public void setPersonalNames(Map<String, Name> personalNames) {
            this.personalNames = personalNames;
        }

        @Override
        public String toString() {
            return "PersonDto{" +
                    "personalNames=" + personalNames +
                    '}';
        }
    }
}
