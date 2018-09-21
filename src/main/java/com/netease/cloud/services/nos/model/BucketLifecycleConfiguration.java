package com.netease.cloud.services.nos.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Container for bucket lifecycle configuration operations.
 */
public class BucketLifecycleConfiguration {

    /**
     * Constant for an enabled rule.
     *
     * @see Rule#setStatus(String)
     */
    public static final String ENABLED = "Enabled";

    /**
     * Constant for a disabled rule.
     *
     * @see Rule#setStatus(String)
     */
    public static final String DISABLED = "Disabled";

    private List<Rule> rules;

    /**
     * Returns the list of rules that comprise this configuration.
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * Sets the rules that comprise this configuration.
     */
    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    /**
     * Sets the rules that comprise this configuration and returns a reference
     * to this object for easy method chaining.
     */
    public BucketLifecycleConfiguration withRules(List<Rule> rules) {
        setRules(rules);
        return this;
    }

    /**
     * Convenience array style method for
     * {@link BucketLifecycleConfiguration#withRules(List)}
     */
    public BucketLifecycleConfiguration withRules(Rule... rules) {
        setRules(Arrays.asList(rules));
        return this;
    }

    /**
     * Constructs a new {@link BucketLifecycleConfiguration} object with the
     * rules given.
     *
     * @param rules
     */
    public BucketLifecycleConfiguration(List<Rule> rules) {
        this.rules = rules;
    }

    public BucketLifecycleConfiguration() {
        super();
    }

    public static class Rule {

        private String id;
        private String prefix;
        private String status;

        /**
         * The time, in days, between when the object is uploaded to the bucket
         * and when it expires. Should not coexist with expirationDate within
         * one lifecycle rule.
         */
        private Integer expirationInDays = null;
        
        /**
         * The expiration date of the object and should not coexist with expirationInDays within
         * one lifecycle rule.
         */
        private Date expirationDate;

        /**
         * Sets the ID of this rule. Rules must be less than 255 alphanumeric
         * characters, and must be unique for a bucket. If you do not assign an
         * ID, one will be generated.
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Sets the key prefix for which this rule will apply.
         */
        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        /**
         * Sets the time, in days, between when an object is uploaded to the
         * bucket and when it expires.
         */
        public void setExpirationInDays(int expirationInDays) {
            this.expirationInDays = expirationInDays;
        }

        /**
         * Returns the ID of this rule.
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the ID of this rule and returns a reference to this object for
         * method chaining.
         *
         * @see Rule#setId(String)
         */
        public Rule withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Returns the key prefix for which this rule will apply.
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * Sets the key prefix for this rule and returns a reference to this
         * object for method chaining.
         *
         * @see Rule#setPrefix(String)
         */
        public Rule withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        /**
         * Returns the time in days from an object's creation to its expiration.
         */
        public Integer getExpirationInDays() {
            return expirationInDays;
        }

        /**
         * Sets the time, in days, between when an object is uploaded to the
         * bucket and when it expires, and returns a reference to this object
         * for method chaining.
         *
         * @see Rule#setExpirationInDays(int)
         */
        public Rule withExpirationInDays(int expirationInDays) {
            this.expirationInDays = expirationInDays;
            return this;
        } 

        /**
         * Returns the status of this rule.
         *
         * @see BucketLifecycleConfiguration#DISABLED
         * @see BucketLifecycleConfiguration#ENABLED
         */
        public String getStatus() {
            return status;
        }

        /**
         * Sets the status of this rule.
         *
         * @see BucketLifecycleConfiguration#DISABLED
         * @see BucketLifecycleConfiguration#ENABLED
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * Sets the status of this rule and returns a reference to this object
         * for method chaining.
         *
         * @see Rule#setStatus(String)
         * @see BucketLifecycleConfiguration#DISABLED
         * @see BucketLifecycleConfiguration#ENABLED
         */
        public Rule withStatus(String status) {
            setStatus(status);
            return this;
        }

        /**
         * Sets the expiration date of the object.
         */
        public void setExpirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
        }

        /**
         * Returns the expiration date of the object.
         */
        public Date getExpirationDate() {
            return this.expirationDate;
        }

        /**
         * Sets the expiration date of the object and returns a reference to this
         * object(Rule) for method chaining.
         */
        public Rule withExpirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }
    }
}