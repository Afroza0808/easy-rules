/*
 * The MIT License
 *
 *  Copyright (c) 2021, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.jeasy.rules.support.reader;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jeasy.rules.api.Rule;
import org.jeasy.rules.support.RuleDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class RuleDefinitionReaderTest {

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                { new YamlRuleDefinitionReader(), "yml" },
                { new JsonRuleDefinitionReader(), "json" },
        });
    }

    @Parameterized.Parameter(0)
    public RuleDefinitionReader ruleDefinitionReader;

    @Parameterized.Parameter(1)
    public String fileExtension;


    @Test
    public void testRuleDefinitionReadingFromFile() throws Exception {

        List<RuleDefinition> ruleDefinitions =
                readRules("adult-rule");           //replace
        assertThat(ruleDefinitions).hasSize(1);
        RuleDefinition adultRuleDefinition = ruleDefinitions.get(0);

        assertAdultRule(adultRuleDefinition);       //replace

    }

    @Test
    public void testRuleDefinitionReadingFromString() throws Exception {
        // given
        Path ruleDescriptor = Paths.get("src/test/resources/adult-rule." + fileExtension);
        String adultRuleDescriptor = new String(Files.readAllBytes(ruleDescriptor));

        // when
        List<RuleDefinition> ruleDefinitions = ruleDefinitionReader.read(new StringReader(adultRuleDescriptor));

        // then
        assertThat(ruleDefinitions).hasSize(1);        //replace
        RuleDefinition adultRuleDefinition = ruleDefinitions.get(0);
        assertAdultRule(adultRuleDefinition);                  //replace

    }

    @Test
    public void testRuleDefinitionReading_withDefaultValues() throws Exception {
        // given

        List<RuleDefinition> ruleDefinitions =             //replace
                readRules("adult-rule");
        // then
        assertThat(ruleDefinitions).hasSize(1);
        RuleDefinition adultRuleDefinition = ruleDefinitions.get(0);
        assertAdultRule(adultRuleDefinition);            //replace

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRuleDefinitionReading_whenNoCondition() throws Exception {

        List<RuleDefinition> ruleDefinitions =          //replace
                readRules("adult-rule");
        // then
        // expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRuleDefinitionReading_whenNoActions() throws Exception {

        List<RuleDefinition> ruleDefinitions =
                readRules("adult-rule");
        // then
        // expected exception
    }

    @Test
    public void testRulesDefinitionReading() throws Exception {

        List<RuleDefinition> ruleDefinitions =
                readRules("adult-rule");                 //replace
        // then
        assertThat(ruleDefinitions).hasSize(2);
        RuleDefinition ruleDefinition = ruleDefinitions.get(0);
        assertAdultRule(ruleDefinition);                      //replace

        ruleDefinition = ruleDefinitions.get(1);
        assertAdultRule(ruleDefinition);                 //replace

    }
    @Test
    public void testEmptyRulesDefinitionReading() throws Exception {

        List<RuleDefinition> ruleDefinitions =
                readRules("adult-rule");
        // then

    }
    @Test
    public void testRuleDefinitionReading_withCompositeAndBasicRules() throws Exception {
        List<RuleDefinition> ruleDefinitions =
                readRules("adult-rule");
        RuleDefinition ruleDefinition = ruleDefinitions.get(0);
        assertAdultRule(ruleDefinition);             //replace

        List<RuleDefinition> subrules = ruleDefinition.getComposingRules();
        assertThat(subrules).hasSize(2);

        RuleDefinition subrule = subrules.get(0);
        assertThat(subrule.getName()).isEqualTo("Time is evening");
        assertThat(subrule.getDescription()).isEqualTo("If it's later than 7pm");
        assertThat(subrule.getPriority()).isEqualTo(1);

        subrule = subrules.get(1);
        assertThat(subrule.getName()).isEqualTo("Movie is rated R");
        assertThat(subrule.getDescription()).isEqualTo("If the movie is rated R");
        assertThat(subrule.getPriority()).isEqualTo(1);

        ruleDefinition = ruleDefinitions.get(1);
        assertAdultRule(ruleDefinition);                    //replace
    }
    private List<RuleDefinition> readRules(String fileName) throws Exception {

        File file = new File(
                "src/test/resources/" +
                        fileName + "." + fileExtension);     // add for duplicate logic and long method

        try (FileReader reader = new FileReader(file)) {
            return ruleDefinitionReader.read(reader);
        }

    }
    private void assertAdultRule(RuleDefinition ruleDefinition) {
        assertThat(ruleDefinition).isNotNull();
        assertThat(ruleDefinition.getName()).isEqualTo("adult rule");        // add for duplicate logic and long method
        assertThat(ruleDefinition.getDescription())
                .isEqualTo("when age is greater than 18, then mark as adult");
        assertThat(ruleDefinition.getPriority()).isEqualTo(1);
        assertThat(ruleDefinition.getCondition()).isEqualTo("person.age > 18");
        assertThat(ruleDefinition.getActions())
                .isEqualTo(Collections.singletonList("person.setAdult(true);"));
        }

    }

