/*
 * Broker Report Parser API
 * Copyright (C) 2021  Vitalii Ananev <an-vitek@ya.ru>
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
 */

package org.spacious_team.broker.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode
public class SecurityEventCashFlow {
    //@Nullable // autoincrement
    private final Integer id;

    @NotNull
    private final String portfolio;

    @NotNull
    private final Instant timestamp;

    @NotNull
    private final String security;

    @NotNull
    private final Integer count;

    @NotNull
    @JsonProperty("event-type")
    private final CashFlowType eventType;

    @NotNull
    private final BigDecimal value;

    @Builder.Default
    private final String currency = "RUR";

    /**
     * Checks DB unique index constraint
     */
    public static boolean checkEquality(SecurityEventCashFlow cash1, SecurityEventCashFlow cash2) {
        return cash1.getPortfolio().equals(cash2.getPortfolio()) &&
                cash1.getTimestamp().equals(cash2.getTimestamp()) &&
                cash1.getEventType().equals(cash2.getEventType()) &&
                cash1.getSecurity().equals(cash2.getSecurity());
    }

    /**
     * Merge information of two objects with equals by {@link #checkEquality(SecurityEventCashFlow, SecurityEventCashFlow)}
     */
    public static Collection<SecurityEventCashFlow> mergeDuplicates(SecurityEventCashFlow cash1, SecurityEventCashFlow cash2) {
        if (!String.valueOf(cash1.getCurrency())
                .equals(String.valueOf(cash2.getCurrency()))) {
            throw new RuntimeException("Не могу объединить выплаты по ЦБ, разные валюты: " + cash1 + " и " + cash2);
        } else if (!String.valueOf(cash1.getCount())
                .equals(String.valueOf(cash2.getCount()))) {
            throw new RuntimeException("Не могу объединить выплаты по ЦБ, разное количество ЦБ: " + cash1 + " и " + cash2);
        }
        return Collections.singletonList(cash1.toBuilder()
                .value(cash1.getValue().add(cash2.getValue()))
                .build());
    }
}
