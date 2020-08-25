/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.utils;

import com.badlogic.gdx.utils.Json;

import org.junit.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestTimestampSerializer {

    @Test
    public void testTimestampSerializer() {
        Date date = new Date();
        Long currentTime = date.getTime();
        Timestamp timestamp = new Timestamp(currentTime);
        TimestampSerializer timestampSerialize = new TimestampSerializer(timestamp);
        Json json = new Json();
        String jsonPretty = json.prettyPrint(timestampSerialize);
        TimestampSerializer timestampSerializer =
                json.fromJson(TimestampSerializer.class, jsonPretty);
        assertThat(timestampSerialize.getTimestamp(),
                is(equalTo(timestampSerializer.getTimestamp())));
    }

    @Test
    public void testGetYearFromTimestampeSerializer() {
        Date date = new Date();
        Long currentTime = date.getTime();
        Timestamp timestamp = new Timestamp(currentTime);
        TimestampSerializer timestampSerializer = new TimestampSerializer(timestamp);
        String year = timestampSerializer.getYear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String yearFromDate = dateFormat.format(date);
        assertThat(year, is(equalTo(yearFromDate)));
    }
}
