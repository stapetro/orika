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

    /**
     * !
     */
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
    }

    public static class PersonDto {
        private Map<String, Name> personalNames;

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
