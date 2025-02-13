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

package com.vaticle.typedb.core.concept.type;

import com.vaticle.typedb.core.common.iterator.sorted.SortedIterator.Forwardable;
import com.vaticle.typedb.core.common.parameters.Order;
import com.vaticle.typedb.core.common.parameters.Concept.Existence;
import com.vaticle.typedb.core.common.parameters.Concept.Transitivity;
import com.vaticle.typedb.core.concept.thing.Relation;


public interface RelationType extends ThingType {

    @Override
    RelationType getSupertype();

    @Override
    Forwardable<? extends RelationType, Order.Asc> getSupertypes();

    @Override
    Forwardable<? extends RelationType, Order.Asc> getSubtypes();

    @Override
    Forwardable<? extends RelationType, Order.Asc> getSubtypes(Transitivity transitivity);

    @Override
    Forwardable<? extends Relation, Order.Asc> getInstances();

    @Override
    Forwardable<? extends Relation, Order.Asc> getInstances(Transitivity transitivity);

    void setSupertype(RelationType superType);

    void setRelates(String roleLabel);

    void setRelates(String roleLabel, String overriddenLabel);

    void unsetRelates(String roleLabel);

    Forwardable<? extends RoleType, Order.Asc> getRelates();

    Forwardable<? extends RoleType, Order.Asc> getRelates(Transitivity transitivity);

    RoleType getRelates(String roleLabel);

    RoleType getRelates(Transitivity transitivity, String roleLabel);

    RoleType getRelatesOverridden(String roleLabel);

    Relation create();

    Relation create(Existence existence);
}
