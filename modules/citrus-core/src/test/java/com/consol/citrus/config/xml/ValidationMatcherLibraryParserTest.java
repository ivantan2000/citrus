/*
 * Copyright 2006-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.config.xml;

import com.consol.citrus.testng.AbstractBeanDefinitionParserTest;
import com.consol.citrus.validation.matcher.CustomValidationMatcher;
import com.consol.citrus.validation.matcher.ValidationMatcherLibrary;
import com.consol.citrus.validation.matcher.core.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * @author Christoph Deppisch
 * @since 2.0
 */
public class ValidationMatcherLibraryParserTest extends AbstractBeanDefinitionParserTest {

    @BeforeClass
    @Override
    protected void parseBeanDefinitions() {
    }

    @Test
    public void testValidationMatcherParser() throws Exception {
        beanDefinitionContext = createApplicationContext("context");
        Map<String, ValidationMatcherLibrary> matcherLibraries = beanDefinitionContext.getBeansOfType(ValidationMatcherLibrary.class);

        Assert.assertEquals(matcherLibraries.size(), 2L);

        ValidationMatcherLibrary matcherLibraryBean = matcherLibraries.get("matcherLib");
        Assert.assertEquals(matcherLibraryBean.getName(), "matcherLib");
        Assert.assertEquals(matcherLibraryBean.getPrefix(), "foo");
        Assert.assertEquals(matcherLibraryBean.getMembers().size(), 3L);
        Assert.assertEquals(matcherLibraryBean.getMembers().get("start").getClass(), StartsWithValidationMatcher.class);
        Assert.assertEquals(matcherLibraryBean.getMembers().get("end").getClass(), EndsWithValidationMatcher.class);
        Assert.assertEquals(matcherLibraryBean.getMembers().get("custom").getClass(), CustomValidationMatcher.class);

        matcherLibraryBean.getMembers().get("custom").validate("field", "Hello Citrus!", "Hello Citrus!", context);

        matcherLibraryBean = matcherLibraries.get("matcherLib2");
        Assert.assertEquals(matcherLibraryBean.getName(), "matcherLib2");
        Assert.assertEquals(matcherLibraryBean.getPrefix(), "bar");
        Assert.assertEquals(matcherLibraryBean.getMembers().size(), 2L);
        Assert.assertEquals(matcherLibraryBean.getMembers().get("isNumber").getClass(), IsNumberValidationMatcher.class);
        Assert.assertEquals(matcherLibraryBean.getMembers().get("custom").getClass(), CustomValidationMatcher.class);

        matcherLibraryBean.getMembers().get("custom").validate("field", "Hello Citrus!", "Hello Citrus!", context);
    }
}
