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

package ma.glasnost.orika.test.constructor;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingException;
import ma.glasnost.orika.constructor.ConstructorResolverStrategy;
import ma.glasnost.orika.constructor.PrefixParamConstructorResolverStrategy;
import ma.glasnost.orika.converter.builtin.DateToStringConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.test.MappingUtil;
import ma.glasnost.orika.test.common.types.TestCaseClasses.*;
import ma.glasnost.orika.test.constructor.TestCaseClasses.*;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.net.URLStreamHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConstructorMappingTestCase {

    private static final String DATE_CONVERTER = "dateConverter";
    private static final String DATE_PATTERN = "dd/MM/yyyy";

    @Test
    public void testSimpleCase() throws Throwable {

        final SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);
        MapperFactory factory = MappingUtil.getMapperFactory();

        factory.classMap(PersonVO.class, Person.class)
                .fieldMap("dateOfBirth", "date")
                .converter(DATE_CONVERTER)
                .add()
                .byDefault()
                .register();

        factory.getConverterFactory().registerConverter(DATE_CONVERTER, new DateToStringConverter(DATE_PATTERN));

        Person person = new Person();
        person.setFirstName("Abdelkrim");
        person.setLastName("EL KHETTABI");
        person.setDate(df.parse("01/01/1980"));
        person.setAge(31L);

        PersonVO vo = factory.getMapperFacade().map(person, PersonVO.class);

        Assert.assertEquals(person.getFirstName(), vo.getFirstName());
        Assert.assertEquals(person.getLastName(), vo.getLastName());
        Assert.assertEquals(person.getAge(), Long.valueOf(vo.getAge()));
        Assert.assertEquals("01/01/1980", vo.getDateOfBirth());
    }


    @Test
    public void testFindConstructor() throws Throwable {
    	final SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);
        MapperFactory factory = MappingUtil.getMapperFactory();

        factory.classMap(PersonVO3.class, Person.class)
                .fieldMap("dateOfBirth", "date").converter(DATE_CONVERTER).add()
                .byDefault()
                .register();
        factory.getConverterFactory().registerConverter(DATE_CONVERTER, new DateToStringConverter(DATE_PATTERN));


        Person person = new Person();
        person.setFirstName("Abdelkrim");
        person.setLastName("EL KHETTABI");
        person.setDate(df.parse("01/01/1980"));
        person.setAge(31L);

        PersonVO3 vo = factory.getMapperFacade().map(person, PersonVO3.class);

        Assert.assertEquals(person.getFirstName(), vo.getFirstName());
        Assert.assertEquals(person.getLastName(), vo.getLastName());
        Assert.assertEquals(person.getAge(), Long.valueOf(vo.getAge()));
        Assert.assertEquals("01/01/1980", vo.getDateOfBirth());
    }

    public static long yearsDifference(final Date start, final Date end) {
		long diff = end.getTime() - start.getTime();
		return diff / TimeUnit.SECONDS.toMillis(60*60*24*365);
	}

    @Test
    public void testFindConstructor2() throws Throwable {
    	final SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);
        MapperFactory factory = MappingUtil.getMapperFactory();

        factory.classMap(PersonVO3.class, Person.class)
            .field("firstName", "firstName")
            .field("lastName", "lastName")
            .field("dateOfBirth", "date")
            .register();
        factory.getConverterFactory().registerConverter(DATE_CONVERTER, new DateToStringConverter(DATE_PATTERN));

        Person person = new Person();
        person.setFirstName("Abdelkrim");
        person.setLastName("EL KHETTABI");
        person.setDate(df.parse("01/01/1980"));

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 1980);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);

        person.setAge(yearsDifference(c.getTime(), new Date()));

        PersonVO3 vo = factory.getMapperFacade().map(person, PersonVO3.class);

        Assert.assertEquals(person.getFirstName(), vo.getFirstName());
        Assert.assertEquals(person.getLastName(), vo.getLastName());
		Assert.assertEquals(person.getAge(), Long.valueOf(vo.getAge()));
        Assert.assertEquals("01/01/1980", vo.getDateOfBirth());
    }

    @Test
    public void testAutomaticCaseWithHint() throws Throwable {

        MapperFactory factory = MappingUtil.getMapperFactory();

		factory.registerDefaultFieldMapper((fromProperty, fromPropertyType) -> {
			if ("dateOfBirth".equals(fromProperty)) {
				return "date";
			} else if("date".equals(fromProperty)) {
				return "dateOfBirth";
			}
			return null;
		});

		factory.getConverterFactory().registerConverter(new DateToStringConverter(DATE_PATTERN));


		Person person = newPerson();
		final SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);
		person.setDate(df.parse("01/01/1980"));

        PersonVO vo = factory.getMapperFacade().map(person, PersonVO.class);

        Assert.assertEquals(person.getFirstName(), vo.getFirstName());
        Assert.assertEquals(person.getLastName(), vo.getLastName());
		Assert.assertEquals(person.getAge(), Long.valueOf(vo.getAge()));
        Assert.assertEquals("01/01/1980", vo.getDateOfBirth());
    }

    @Test
    public void testPrimitiveToPrimitiveTypes() {

    	PrimitiveHolder primitiveHolder =
    			new PrimitiveHolder(
    					Short.MAX_VALUE,
    					Integer.MAX_VALUE,
    					Long.MAX_VALUE,
    					Float.MAX_VALUE,
    					Double.MAX_VALUE,
    					Character.MAX_VALUE,
    					true,
    					Byte.MAX_VALUE);

    	MapperFactory factory = MappingUtil.getMapperFactory();

    	PrimitiveHolderDTO dto = factory.getMapperFacade().map(primitiveHolder, PrimitiveHolderDTO.class);

    	assertValidMapping(primitiveHolder,dto);

    	PrimitiveHolder mapBack = factory.getMapperFacade().map(dto, PrimitiveHolder.class);

    	assertValidMapping(mapBack, dto);
    }

    @Test
    public void testPrimitiveToWrapperTypes() {

    	PrimitiveHolder primitiveHolder =
    			new PrimitiveHolder(
    					Short.MAX_VALUE,
    					Integer.MAX_VALUE,
    					Long.MAX_VALUE,
    					Float.MAX_VALUE,
    					Double.MAX_VALUE,
    					Character.MAX_VALUE,
    					true,
    					Byte.MAX_VALUE);

    	MapperFactory factory = MappingUtil.getMapperFactory();

    	PrimitiveWrapperHolder wrapper = factory.getMapperFacade().map(primitiveHolder, PrimitiveWrapperHolder.class);

    	assertValidMapping(wrapper, primitiveHolder);

    	PrimitiveHolder mapBack = factory.getMapperFacade().map(wrapper, PrimitiveHolder.class);

    	assertValidMapping(wrapper, mapBack);
    }

    @Test
    public void testWrapperToWrapperTypes() {

    	PrimitiveWrapperHolder primitiveHolder =
    			new PrimitiveWrapperHolder(
    					Short.MAX_VALUE,
    					Integer.MAX_VALUE,
    					Long.MAX_VALUE,
    					Float.MAX_VALUE,
    					Double.MAX_VALUE,
    					Character.MAX_VALUE,
    					true,
    					Byte.MAX_VALUE);

    	MapperFactory factory = MappingUtil.getMapperFactory();

    	PrimitiveWrapperHolderDTO dto = factory.getMapperFacade().map(primitiveHolder, PrimitiveWrapperHolderDTO.class);

    	assertValidMapping(primitiveHolder, dto);

    	PrimitiveWrapperHolder mapBack = factory.getMapperFacade().map(dto, PrimitiveWrapperHolder.class);

    	assertValidMapping(mapBack, dto);
    }

    @Test
    public void testPrimitivePropertiesWithWrapperConstructor() throws Throwable {

        MapperFactory factory = MappingUtil.getMapperFactory();

		factory.registerDefaultFieldMapper((fromProperty, fromPropertyType) -> {
			if ("dateOfBirth".equals(fromProperty)) {
				return "date";
			} else if("date".equals(fromProperty)) {
				return "dateOfBirth";
			}
			return null;
		});

		factory.getConverterFactory().registerConverter(new DateToStringConverter(DATE_PATTERN));

		Person person = newPerson();
		final SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);
		person.setDate(df.parse("01/01/1980"));

        PersonVO2 vo = factory.getMapperFacade().map(person, PersonVO2.class);

        Assert.assertEquals(person.getFirstName(), vo.getFirstName());
        Assert.assertEquals(person.getLastName(), vo.getLastName());
		Assert.assertEquals(person.getAge(), Long.valueOf(vo.getAge()));
        Assert.assertEquals("01/01/1980", vo.getDateOfBirth());

    }

    @Test
    public void testBaseCaseWithCollectionTypes() {

    	List<Book> books = new ArrayList<>(4);

    	Author author1 = new AuthorImpl("Author #1");
    	Author author2 = new AuthorImpl("Author #2");

    	books.add(new BookImpl("Book #1", author1));
    	books.add(new BookImpl("Book #2", author1));
    	books.add(new BookImpl("Book #3", author2));
    	books.add(new BookImpl("Book #4", author2));

    	Library library = new LibraryImpl("Library #1", books);

    	MapperFactory factory = MappingUtil.getMapperFactory();
    	MapperFacade mapper = factory.getMapperFacade();

    	LibraryDTO mapped = mapper.map(library, LibraryDTO.class);

    	assertValidMapping(library,mapped);

    	Library libraryMapBack = mapper.map(mapped, LibraryImpl.class);

    	assertValidMapping(libraryMapBack,mapped);

    }

    @Test
    public void testMappingNestedTypes() {

    	List<BookNested> books = new ArrayList<>(4);

    	AuthorNested author1 = new AuthorNested(new Name("Abdelkrim","EL KHETTABI"));
    	AuthorNested author2 = new AuthorNested(new Name("Bill","Shakespeare"));

    	books.add(new BookNested("Book #1", author1));
    	books.add(new BookNested("Book #2", author1));
    	books.add(new BookNested("Book #3", author2));
    	books.add(new BookNested("Book #4", author2));

    	LibraryNested library = new LibraryNested("Library #1", books);

    	MapperFactory factory = MappingUtil.getMapperFactory();

   		factory.classMap(AuthorNested.class, AuthorDTO.class)
    	        .field("name.fullName", "name")
                .byDefault()
                .register();

    	MapperFacade mapper = factory.getMapperFacade();

    	LibraryDTO mapped = mapper.map(library, LibraryDTO.class);

    	assertValidMapping(library,mapped);

    	/*
    	// this situation is a bit too complicated to handle normally;
    	// how would Orika even know how to create a Name object which takes
    	// in multiple parameters it cannot find on the source object?
    	LibraryNested libraryMapBack = mapper.map(mapped, LibraryNested.class);

    	assertValidMapping(libraryMapBack,mapped);

    	*/
    }


    @Test
    public void testComplexMappingNestedTypes() {


    	PrimitiveNumberHolder numbers =
    			new PrimitiveNumberHolder(
    					Short.MAX_VALUE,
    					Integer.MAX_VALUE,
    					Long.MAX_VALUE,
    					Float.MAX_VALUE,
    					Double.MAX_VALUE);

    	NestedPrimitiveHolder primitiveHolder = new NestedPrimitiveHolder(numbers, Character.MAX_VALUE, Boolean.TRUE, Byte.MAX_VALUE);

    	Holder holder = new Holder(primitiveHolder);

    	MapperFactory factory = MappingUtil.getMapperFactory();

        factory.classMap(NestedPrimitiveHolder.class, PrimitiveWrapperHolder.class)
            .field("numbers.shortValue", "shortValue")
            .field("numbers.intValue", "intValue")
            .field("numbers.longValue", "longValue")
            .field("numbers.floatValue", "floatValue")
            .field("numbers.doubleValue", "doubleValue")
            .byDefault()
            .register();

    	WrapperHolder wrapper = factory.getMapperFacade().map(holder, WrapperHolder.class);

    	assertValidMapping(holder, wrapper);

    }


    public static class URLDto1 {
    	public String protocolX;
    	public String hostX;
    	public int portX;
    	public String fileX;
    }

    public static class URLDto2 {
    	public String protocol;
    	public String host;
    	public String file;
    }

    public static class URLDto3 {
    	public String protocol;
    	public String host;
    	public int port;
    	public String file;
    	public URLStreamHandler handler;
    }

    public static class URLDto4 {
    	public URL context;
    	public String spec;
    }

    @Test
    public void testConstructorsWithoutDebugInfo() {
    	MapperFactory factory = MappingUtil.getMapperFactory();

        factory.classMap(URLDto1.class, URL.class)
            .field("protocolX", "protocol")
            .field("hostX", "host")
            .field("portX", "port")
            .field("fileX", "file")
            .register();
        MapperFacade mapper = factory.getMapperFacade();

    	URLDto1 dto1 = new URLDto1();
    	dto1.protocolX = "http";
    	dto1.hostX = "somewhere.com";
    	dto1.portX = 8080;
    	dto1.fileX = "index.html";

    	URL url = mapper.map(dto1, URL.class);
    	Assert.assertNotNull(url);
    	Assert.assertEquals(dto1.protocolX, url.getProtocol());
    	Assert.assertEquals(dto1.hostX, url.getHost());
    	Assert.assertEquals(dto1.portX, url.getPort());

    }

	@Test
	public void testFindConstructor_mapParamNames_ok() {
		ConstructorResolverStrategy constructorStrategy = new PrefixParamConstructorResolverStrategy();
		DefaultMapperFactory.Builder builder = new DefaultMapperFactory.Builder();
		builder.constructorResolverStrategy(constructorStrategy);
		MapperFactory factory = builder.build();

		testPersonV04Mapping(factory);
	}

	@Test(expected = MappingException.class)
	public void testFindConstructor_doNotMapParamNames_ex() {
		testPersonV04Mapping(MappingUtil.getMapperFactory());
	}

	private void testPersonV04Mapping(MapperFactory factory) {
		Person person = newPerson();

		PersonVO4 vo = factory.getMapperFacade().map(person, PersonVO4.class);

		Assert.assertEquals(person.getFirstName(), vo.getFirstName());
		Assert.assertEquals(person.getLastName(), vo.getLastName());
		Assert.assertEquals(person.getAge(), Long.valueOf(vo.getAge()));
	}

	private Person newPerson() {
		Person person = new Person();
		person.setFirstName("Abdelkrim");
		person.setLastName("EL KHETTABI");
		person.setAge(31L);
		return person;
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Common mapping validations
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void assertValidMapping(Holder holder, WrapperHolder dto) {
    	assertValidMapping(holder.getNested(), dto.getNested());
    }

    private void assertValidMapping(NestedPrimitiveHolder nested, PrimitiveWrapperHolder wrapper) {
    	assertEquals(nested.getNumbers().getShortValue(), wrapper.getShortValue().shortValue());
    	assertEquals(nested.getNumbers().getIntValue(), wrapper.getIntValue().intValue());
    	assertEquals(nested.getNumbers().getLongValue(), wrapper.getLongValue().longValue());
    	assertEquals(nested.getNumbers().getFloatValue(), wrapper.getFloatValue(), 1.0f);
    	assertEquals(nested.getNumbers().getDoubleValue(), wrapper.getDoubleValue(), 1.0d);
    	assertEquals(nested.getCharValue(), wrapper.getCharValue().charValue());
    	assertEquals(nested.isBooleanValue(), wrapper.getBooleanValue().booleanValue());
    	assertEquals(nested.getByteValue(), wrapper.getByteValue().byteValue());
    }

    private void assertValidMapping(PrimitiveHolder primitiveHolder, PrimitiveHolderDTO dto) {
    	assertEquals(primitiveHolder.getShortValue(), dto.getShortValue());
    	assertEquals(primitiveHolder.getIntValue(), dto.getIntValue());
    	assertEquals(primitiveHolder.getLongValue(), dto.getLongValue());
    	assertEquals(primitiveHolder.getFloatValue(), dto.getFloatValue(), 1.0f);
    	assertEquals(primitiveHolder.getDoubleValue(), dto.getDoubleValue(), 1.0d);
    	assertEquals(primitiveHolder.getCharValue(), dto.getCharValue());
    	assertEquals(primitiveHolder.isBooleanValue(), dto.isBooleanValue());
    	assertEquals(primitiveHolder.getByteValue(), dto.getByteValue());
    }

    private void assertValidMapping(PrimitiveWrapperHolder primitiveHolder, PrimitiveWrapperHolderDTO dto) {
    	assertEquals(primitiveHolder.getShortValue(), dto.getShortValue());
    	assertEquals(primitiveHolder.getIntValue(), dto.getIntValue());
    	assertEquals(primitiveHolder.getLongValue(), dto.getLongValue());
    	assertEquals(primitiveHolder.getFloatValue(), dto.getFloatValue(), 1.0f);
    	assertEquals(primitiveHolder.getDoubleValue(), dto.getDoubleValue(), 1.0d);
    	assertEquals(primitiveHolder.getCharValue(), dto.getCharValue());
    	assertEquals(primitiveHolder.getBooleanValue(), dto.getBooleanValue());
    	assertEquals(primitiveHolder.getByteValue(), dto.getByteValue());
    }

    private void assertValidMapping(PrimitiveWrapperHolder wrappers, PrimitiveHolder primitives) {
    	assertEquals(wrappers.getShortValue().shortValue(), primitives.getShortValue());
    	assertEquals(wrappers.getIntValue().intValue(), primitives.getIntValue());
    	assertEquals(wrappers.getLongValue().longValue(), primitives.getLongValue());
    	assertEquals(wrappers.getFloatValue().floatValue(), primitives.getFloatValue(), 1.0f);
    	assertEquals(wrappers.getDoubleValue().doubleValue(), primitives.getDoubleValue(), 1.0d);
    	assertEquals(wrappers.getCharValue().charValue(), primitives.getCharValue());
    	assertEquals(wrappers.getBooleanValue().booleanValue(), primitives.isBooleanValue());
    	assertEquals(wrappers.getByteValue().byteValue(), primitives.getByteValue());
    }


    private void assertValidMapping(Library library, LibraryDTO dto) {

    	assertNotNull(library);
    	assertNotNull(dto);

    	assertNotNull(library.getBooks());
    	assertNotNull(dto.getBooks());

		List<Book> sortedBooks =library.getBooks();

		List<BookDTO> sortedDTOs = dto.getBooks();

    	assertEquals(sortedBooks.size(), sortedDTOs.size());

    	for (int i = 0, count=sortedBooks.size(); i < count; ++i) {
    		Book book = sortedBooks.get(i);
    		BookDTO bookDto = sortedDTOs.get(i);
    		assertValidMapping(book,bookDto);
    	}
    }

    private void assertValidMapping(LibraryNested library, LibraryDTO dto) {

    	assertNotNull(library);
    	assertNotNull(dto);

    	assertNotNull(library.getBooks());
    	assertNotNull(dto.getBooks());

		List<BookNested> sortedBooks = library.getBooks();

		List<BookDTO> sortedDTOs = dto.getBooks();

    	assertEquals(sortedBooks.size(), sortedDTOs.size());

    	for (int i = 0, count=sortedBooks.size(); i < count; ++i) {
    		BookNested book = sortedBooks.get(i);
    		BookDTO bookDto = sortedDTOs.get(i);
    		assertValidMapping(book,bookDto);
    	}
    }

    private void assertValidMapping(Book book, BookDTO dto) {
    	assertNotNull(book);
    	assertNotNull(dto);
    	assertEquals(book.getTitle(), dto.getTitle());
    	assertValidMapping(book.getAuthor(), dto.getAuthor());
    }

    private void assertValidMapping(BookNested book, BookDTO dto) {
    	assertNotNull(book);
    	assertNotNull(dto);
    	assertEquals(book.getTitle(), dto.getTitle());
    	assertValidMapping(book.getAuthor(), dto.getAuthor());
    }

    private void assertValidMapping(Author author, AuthorDTO authorDTO) {
    	assertNotNull(author);
    	assertNotNull(authorDTO);
    	assertEquals(author.getName(),authorDTO.getName());
    }

    private void assertValidMapping(AuthorNested author, AuthorDTO authorDTO) {
    	assertNotNull(author);
    	assertNotNull(authorDTO);
    	assertEquals(author.getName().getFullName(),authorDTO.getName());
    }
}
