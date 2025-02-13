/*
 * Copyright (C) 2022 Vaticle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.vaticle.typedb.core.pattern.variable;

import com.vaticle.typedb.core.common.exception.TypeDBException;
import com.vaticle.typedb.core.common.parameters.Label;
import com.vaticle.typedb.core.pattern.Pattern;
import com.vaticle.typedb.core.pattern.constraint.Constraint;
import com.vaticle.typedb.core.traversal.GraphTraversal;
import com.vaticle.typedb.core.traversal.common.Identifier;
import com.vaticle.typeql.lang.common.Reference;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typedb.core.common.exception.ErrorMessage.Pattern.INVALID_CASTING;

public abstract class Variable implements Pattern {

    private final Set<Label> inferredTypes;
    private final int hash;
    final Identifier.Variable identifier;

    Variable(Identifier.Variable identifier) {
        this.identifier = identifier;
        this.hash = Objects.hash(identifier);
        this.inferredTypes = new HashSet<>();
    }

    public abstract Set<? extends Constraint> constraints();

    public abstract Set<Constraint> constraining();

    public abstract void constraining(Constraint constraint);

    public abstract Identifier.Variable id();

    public Reference reference() {
        return identifier.reference();
    }

    public abstract void addTo(GraphTraversal.Thing traversal);

    public boolean isType() {
        return false;
    }

    public boolean isThing() {
        return false;
    }

    public boolean isValue() {
        return false;
    }

    public TypeVariable asType() {
        throw TypeDBException.of(INVALID_CASTING, className(this.getClass()), className(TypeVariable.class));
    }

    public ThingVariable asThing() {
        throw TypeDBException.of(INVALID_CASTING, className(this.getClass()), className(ThingVariable.class));
    }

    public ValueVariable asValue() {
        throw TypeDBException.of(INVALID_CASTING, className(this.getClass()), className(ValueVariable.class));
    }

    public void addInferredTypes(Label label) {
        inferredTypes.add(label);
    }

    public void addInferredTypes(Set<Label> labels) {
        inferredTypes.addAll(labels);
    }

    public void setInferredTypes(Set<Label> labels) {
        inferredTypes.clear();
        inferredTypes.addAll(labels);
    }

    public void retainInferredTypes(Set<Label> labels) {
        inferredTypes.retainAll(labels);
    }

    public Set<Label> inferredTypes() {
        return inferredTypes;
    }

    @Override
    public String toString() {
        if (identifier.isLabel()) return asType().label().get().properLabel().name();
        else return identifier.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable that = (Variable) o;
        return this.identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
