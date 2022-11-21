# Partner Connect onboarding lifecycle

## Prerequisites
1. You're already a [Partner](https://partners.databricks.com).
2. Your product must already have a validated product integration ("core integration") with Databricks.

## Getting scheduled for onboarding

1. The Databricks team will initially work with you to assess if your product is a fit for Partner Connect.
2. Your team will fill out a [Tech due diligence form](https://docs.google.com/forms/d/e/1FAIpQLSftDtJiqKirOL81_4ZTRM-bu27lmjZcEvOl4mGryyZ1WpTBHg/viewform) to assess technical fit for Partner Connect.
3. Your team will commit engineering resources to completing this integration.
4. Your team will meet with our team to go over technical details and usability requirements of the integration.
5. The Databricks team will select a quarter to schedule engineering resources and to onboard your product to Partner Connect.

## Non-engineering activities needed for launch
While engineering development is in-progress, the following GTM requirements should be actively worked on by Marketing / PDMs:
1. Signing of the legal partnership agreement (PPA).
2. Databricks mini site (e.g. partner.com/databricks).
3. Databricks logo on partner directory.
4. Partner Connect Launch Blog.  Databricks will link from a Group Launch Blog Post.
5. Partner documentation on the integration.

## Onboarding

1. Your team will implement the integration (both API and Frontend user flows).  You'll use the [Partner Connect Certification](README.md#partner-connect-certification) to test the correctness of your API.
2. Your team will submit artifacts using the [Artifact submission form](https://docs.google.com/forms/d/e/1FAIpQLSde_LVKbMeEqT1k1lujtIOPQCzIkEhJpz5JdhdcA613ErsHVA/viewform) to Databricks.
3. The Databricks team will add your tile to Partner Connect.  We will provision a workspace for you to test end-to-end.  This workspace will be the only Production workspace where your tile is visible.
4. Your team will complete validation.  You'll manually execute all the test cases in this documentation.
5. The Databricks team will schedule a video-conference with you to manually run through all the test cases together.  If needed, we'll iterate on problems found and re-schedule another video conference.

## Onboarding end-game

See [Onboarding validation](OnboardingValidationDoc.md) for detailed validation criteria.

1. The Databricks engineering team will sign-off on the Partner Connect integration.
2. The Databricks partner solution architect team will sign-off on the product integration ("core integration") using Partner Connect.
3. The Databricks product team will sign-off on the usability of the Partner Connect integration.
4. Your team will sign-off on the Partner Connect integration.
5. The Databricks team will confirm the signing of the legal partnership agreement.
6. The Databricks team will align with you on a GA launch date at least 1 week in advance.  Other GTM activities like marketing and blog posts are finalized.
7. During the week before GA launch date, the Databricks team will roll out your tile to Production region-by-region.  The roll out will complete at least 1 day prior to the GA launch date.

## Ongoing maintenance of the integration

Databricks is always adding new features to both the general Databricks product as well as the specific Partner Connect feature set.  Our team will work with yours to develop a roadmap for keeping the integration up-to-date.

In addition, there are several regular maintenance tasks that may arise from time-to-time.  Examples may include rotating credentials, escalations, or changes in partner configuration (e.g. a partner adds support for GCP, a partner adds support for Unity Catalog, or a partner changes their hostname).

Both parties are responsible for fixing bugs after launch.  Databricks will share a SOP for handling bugs after launch.
