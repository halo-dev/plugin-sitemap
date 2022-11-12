package run.halo.sitemap;

/**
 * How frequently the page is likely to change.
 * This value provides general information to search engines and may not correlate exactly to
 * how often they crawl the page. Valid values are:
 * <ul>
 *     <li>always</li>
 *     <li>hourly</li>
 *     <li>daily</li>
 *     <li>weekly</li>
 *     <li>monthly</li>
 *     <li>yearly</li>
 *     <li>never</li>
 * </ul>
 * The value "always" should be used to describe documents that change each time they are accessed.
 * The value "never" should be used to describe archived URLs.
 * Please note that the value of this tag is considered a hint and not a command.
 * Even though search engine crawlers may consider this information when making decisions, they may
 * crawl pages marked "hourly" less frequently than that, and they may crawl pages marked "yearly"
 * more frequently than that. Crawlers may periodically crawl pages marked "never" so that they
 * can handle unexpected changes to those pages.
 *
 * @author guqing
 * @since 1.0.0
 */
public enum ChangeFreqEnum {
    ALWAYS,
    HOURLY,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    NEVER
}
