package org.javers.core.graph

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.metamodel.type.*;
import org.javers.core.model.DummyUser
import org.javers.core.metamodel.object.Cdo
import org.javers.core.metamodel.object.InstanceId
import spock.lang.Specification

import static org.javers.test.builder.DummyUserBuilder.dummyUser


abstract class ObjectNodeTest extends Specification {

    protected TypeFactory typeFactory

    def "should hold Entity reference"() {
        given:
        def cdo = dummyUser().build()
        def entity = typeFactory.createEntity(DummyUser)

        when:
        def wrapper = new ObjectNode(cdo, entity)

        then:
        wrapper.managedType == entity
    }

    
    def "should hold GlobalId"() {
        given:
        def cdo = dummyUser().withName("Mad Kaz").build()
        def entity = typeFactory.createEntity(DummyUser)

        when:
        ObjectNode wrapper = new ObjectNode(cdo, entity)

        then:
        wrapper.globalId == InstanceId.createFromInstance(cdo, entity)
    }
    
    def "should hold Cdo reference"() {
        given:
        def cdo = dummyUser().build()
        def entity = typeFactory.createEntity(DummyUser)

        when:
        def wrapper = new ObjectNode(cdo, entity)

        then:
        wrapper.wrappedCdo().get() == cdo
    }

    
    def "should throw exception when Entity without id"() {
        given:
        def cdo = new DummyUser()
        def entity = typeFactory.createEntity(DummyUser)

        when:
        new ObjectNode(cdo, entity)

        then:
        JaversException exception = thrown(JaversException)
        exception.code == JaversExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID
    }

    
    def "should delegate equals() and hashCode() to CDO"() {
        when:
        def first = new ObjectNode(new DummyUser("Mad Kax"), typeFactory.createEntity(DummyUser))
        def second = new ObjectNode(new DummyUser("Mad Kax"), typeFactory.createEntity(DummyUser))

        then:
        first.hashCode() == second.hashCode()
        first == second
    }

    
    def "should not be equal when different CDO ids"() {
        when:
        def first = new ObjectNode(new DummyUser("stach"), typeFactory.createEntity(DummyUser))
        def second = new ObjectNode(new DummyUser("Mad Kaz 1"), typeFactory.createEntity(DummyUser))

        then:
        first != second
    }

    
    def "should have reflexive equals method"() {
        when:
        def objectNode = new ObjectNode(new DummyUser("Mad Kax"), typeFactory.createEntity(DummyUser))

        then:
        objectNode == objectNode
    }

    
    def "should have symmetric and transitive equals method"() {
        when:
        ObjectNode first = new ObjectNode(new DummyUser("Mad Kax"), typeFactory.createEntity(DummyUser))
        ObjectNode second = new ObjectNode(new DummyUser("Mad Kax"), typeFactory.createEntity(DummyUser))
        ObjectNode third = new ObjectNode(new DummyUser("Mad Kax"), typeFactory.createEntity(DummyUser))

        then:
        first == second
        second == third
        first == third
    }

    
    def "should return false when equals method has null arg"() {
        when:
        ObjectNode first = new ObjectNode(new DummyUser("Mad Kax"), typeFactory.createEntity(DummyUser))

        then:
        first != null
    }

    def "should delegate equals and hash code to Cdo"() {
        when:
        Cdo mockedCdo = Mock()
        ObjectNode node1 = new ObjectNode(mockedCdo)
        ObjectNode node2 = new ObjectNode(mockedCdo)

        then:
        node1.hashCode() == mockedCdo.hashCode()
        node1 == node2
    }
}
