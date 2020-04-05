package ma.glasnost.orika.test.community;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.test.MappingUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/orika-mapper/orika/issues/322">https://github.com/orika-mapper/orika/issues/322</a>
 */
public class Issue337Test {

    @Test
    public void testDeepMapping() {

        MapperFactory mapperFactory = MappingUtil.getMapperFactory();
        mapperFactory.classMap(MatcherDTO.class, Matcher.class)
                .field("matchExpression", "match")
                .byDefault()
                .register();
        mapperFactory.classMap(Operation.class, Operation.class)
                .exclude("resultType")
                .byDefault()
                .register();
        /*Type<List<Operation>> opsType = new TypeBuilder<List<Operation>>() {
        }.build();
        mapperFactory.classMap(opsType, opsType)
                .byDefault()
                .register();*/

        Operation op = new AttributeReference();
        List<Object> inputs = new ArrayList<>();
        op.inputs = inputs;
        inputs.add("jdbc-resource");
        inputs.add("login");
        Operation op1 = new AttributeReference();
        List<Object> input1 = new ArrayList<>();
        op1.inputs = input1;
        input1.add("ad-resource");
        input1.add("userPrincipalName");
        Operation rootOp = new Equals();
        List<Object> rootIns = new ArrayList<>();
        rootOp.inputs = rootIns;
        rootIns.add(op);
        rootIns.add(op1);
        MatcherDTO matcherDto = new MatcherDTO();
        matcherDto.name = "testName";
        matcherDto.matchExpression = rootOp;

        Matcher dest = mapperFactory.getMapperFacade().map(matcherDto, Matcher.class);
        System.out.println("src: " + matcherDto);
        System.out.println("dst" + dest);
    }

    public static class Matcher {
        public String name;
        public Operation match;

        @Override
        public String toString() {
            return "Matcher{" +
                    "name='" + name + '\'' +
                    ", match=" + match +
                    '}';
        }

    }

    public static class MatcherDTO {
        public String name;
        public Operation matchExpression;

        @Override
        public String toString() {
            return "MatcherDTO{" +
                    "name='" + name + '\'' +
                    ", matchExpression=" + matchExpression +
                    '}';
        }
    }

    public abstract static class Operation<I, R> {

        // Parameters to the operation, which may be
        // Operation implementations or boxed
        // primitives
        public List<I> inputs;

        public abstract R getResultType();

        @Override
        public String toString() {
            return "Operation{" +
                    "inputs=" + inputs +
                    '}';
        }
    }

    public static class AttributeReference extends Operation {
        @Override
        public Class getResultType() {
            return String.class;
        }
    }

    public static class Equals extends Operation {
        @Override
        public Class getResultType() {
            return Integer.class;
        }
    }
}
