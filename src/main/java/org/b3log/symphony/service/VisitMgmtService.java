/*
 * Symphony - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Copyright (C) 2012-2018, b3log.org & hacpai.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.symphony.service;

import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.CompositeFilterOperator;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.annotation.Transactional;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.Visit;
import org.b3log.symphony.repository.VisitRepository;
import org.json.JSONObject;

/**
 * Visit management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 27, 2018
 * @since 3.2.0
 */
@Service
public class VisitMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(VisitMgmtService.class);

    /**
     * Visit repository.
     */
    @Inject
    private VisitRepository visitRepository;

    /**
     * Adds the specified visit.
     *
     * @param visit the specified visit
     * @return {@code true} if visited before, returns {@code false} otherwise
     */
    @Transactional
    public boolean add(final JSONObject visit) {
        try {
            final String url = visit.optString(Visit.VISIT_URL);
            final String ip = visit.optString(Visit.VISIT_IP);
            final Query query = new Query().setFilter(CompositeFilterOperator.and(
                    new PropertyFilter(Visit.VISIT_URL, FilterOperator.EQUAL, url),
                    new PropertyFilter(Visit.VISIT_IP, FilterOperator.EQUAL, ip))).setPageCount(1);

            final long count = visitRepository.count(query);
            if (0 < count) {
                return true;
            }

            visitRepository.add(visit);

            return false;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Adds a visit failed", e);

            return true;
        }
    }
}
