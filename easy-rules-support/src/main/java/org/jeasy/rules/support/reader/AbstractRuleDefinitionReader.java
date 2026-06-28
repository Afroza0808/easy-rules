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

import org.jeasy.rules.api.Rule;
import org.jeasy.rules.support.RuleDefinition;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class for {@link RuleDefinitionReader}s.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public abstract class AbstractRuleDefinitionReader implements RuleDefinitionReader {

    public List<RuleDefinition> read(Reader reader) throws Exception {
        List<RuleDefinition> ruleDefinitions = new ArrayList<>();
        Iterable<Map<String, Object>> rules = loadRules(reader);
        for (Map<String, Object> rule : rules) {
            ruleDefinitions.add(createRuleDefinition(rule));
        }
        return ruleDefinitions;
    }

    /**
     * Load rules from the given reader as an iterable of Maps.
     *
     * @param reader to read rules from
     * @return an iterable of rule Maps
     * @throws Exception if unable to load rules
     */
    protected abstract Iterable<Map<String, Object>> loadRules(Reader reader) throws Exception;

    /**
     * Create a rule definition.
     *
     * @param map of rule properties
     * @return a rule definition
     */
    private void populateBasicProperties(RuleDefinition ruleDefinition,
                                         Map<String, Object> map) {

        String name = (String) map.get("name");
        ruleDefinition.setName(name != null ? name : Rule.DEFAULT_NAME);   //added

        String description = (String) map.get("description");
        ruleDefinition.setDescription(description != null ? description : Rule.DEFAULT_DESCRIPTION);

        Integer priority = (Integer) map.get("priority");
        ruleDefinition.setPriority(priority != null ? priority : Rule.DEFAULT_PRIORITY);
    }
    private String validateCondition(Map<String, Object> map,
                                     String compositeRuleType) {

        String condition = (String) map.get("condition");          //added

        if (condition == null && compositeRuleType == null) {
            throw new IllegalArgumentException("The rule condition must be specified");
        }
        return condition;
    }
    private List<String> validateActions(Map<String, Object> map,
                                         String compositeRuleType) {

        List<String> actions = (List<String>) map.get("actions");

        if ((actions == null || actions.isEmpty())
                && compositeRuleType == null) {

            throw new IllegalArgumentException(
                    "The rule action(s) must be specified");        //added

        }
        return actions;
    }
    private void processComposingRules(RuleDefinition ruleDefinition,
                                       Map<String, Object> map,
                                       String compositeRuleType) {

        List<Object> composingRules =
                (List<Object>) map.get("composingRules");

        validateComposingRules(composingRules, compositeRuleType);

        if (composingRules == null) {
            return;
        }
                                                                  //added
        List<RuleDefinition> composingRuleDefinitions =
                new ArrayList<>();

        for (Object rule : composingRules) {

            Map<String, Object> composingRuleMap =
                    (Map<String, Object>) rule;

            composingRuleDefinitions.add(
                    createRuleDefinition(composingRuleMap));

        }
        ruleDefinition.setComposingRules(composingRuleDefinitions);
        ruleDefinition.setCompositeRuleType(compositeRuleType);

    }
    private void validateComposingRules(List<Object> composingRules,
                                        String compositeRuleType) {
        if ((composingRules != null && !composingRules.isEmpty())
                && compositeRuleType == null) {

            throw new IllegalArgumentException(
                    "Non-composite rules cannot have composing rules");       //added

        }
        if ((composingRules == null || composingRules.isEmpty())
                && compositeRuleType != null) {

            throw new IllegalArgumentException(
                    "Composite rules must have composing rules specified");
        }
    }
    protected RuleDefinition createRuleDefinition(Map<String, Object> map) {
        RuleDefinition ruleDefinition = new RuleDefinition();
        String compositeRuleType = (String) map.get("compositeRuleType");    //replace

        String condition = validateCondition(map, compositeRuleType);   //replace
        ruleDefinition.setCondition(condition);

        List<String> actions = validateActions(map, compositeRuleType);
        ruleDefinition.setActions(actions);    //replace

        List<Object> composingRules = (List<Object>) map.get("composingRules");

        processComposingRules(ruleDefinition, map, compositeRuleType);   //replace
        return ruleDefinition;

    }
}
