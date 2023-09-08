# Partner Connect onboarding lifecycle

## Prerequisites
1. You're already a [Partner](https://partners.databricks.com).
2. Your product must already have a validated product integration ("core integration") with Databricks.

## Getting scheduled for onboarding

1. The Databricks team will initially work with you to assess if your product is a fit for Partner Connect.
2. Your team will fill out a _Technical Due Diligence_ form provided by your PDM to assess technical fit for Partner Connect.
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

1. Review the [UX Guidelines](#ux-guidelines) Once your design is available, please review the mocks or video with the Databricks team.
2. Your team will implement the integration (both API and Frontend user flows).  You'll use the [Partner Connect Certification](README.md#partner-connect-certification) to test the correctness of your API.
3. After discussing with the Databricks team, you may use the [Self-Testing Partner CLI](self-testing-partner-cli/README.md) to do initial testing of your Partner Connect integration.  The CLI will create a testing tile visible only in your workspace.
4. Your team will submit artifacts using the [Artifact submission form](https://docs.google.com/forms/d/e/1FAIpQLSc2vcAqAOVlE7Llo3GMhLrK3klzYXQ5LeWyqaR6L20RjHpygQ/viewform?usp=sf_link) to Databricks.
5. The Databricks team will add your tile to Partner Connect.  We will provision a workspace for you to test end-to-end.  This workspace will be the only Production workspace where your tile is visible.
6. Your team will complete validation.  You'll manually execute all the test cases in this documentation.
7. The Databricks team will schedule a video-conference with you to manually run through all the test cases together.  If needed, we'll iterate on problems found and re-schedule another video conference.

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

Both parties are responsible for fixing bugs after launch. Please follow this [SOP](https://docs.google.com/document/d/1sZVMdGCHYrWFYUw-_uy8hPRzL-wJm9ActYh2L02SWjk/edit?usp=sharing) for handling Partner Connect integration issues. 


## UX Guidelines

These are guidelines for the experience after a user lands on your product through Partner Connect. The goal is for users to complete the signup process and start using your product as soon as possible. 

1. Sign up guidelines
   * Do:
     * Keep it simple! Minimize the information the user needs to enter.
       * Only collect what is necessary to start the trial (email and password)
     * Use/pre-populate the user’s email address sent by Partner Connect
   * Don’t:
     * Collect info that is not needed for the trial such as address, domain name, etc.
     * Direct the user to a sign-in page after they have filled in the signup information
       * User should land on the product page after signup
     * Have an email verification step that blocks further actions
       * The email address you receive from Partner Connect has already been verified by Databricks
       * If email verification is necessary, use a non-blocking method – let the user proceed and verify later
     * Require two-factor authentication setup
     * Collect payment information
       * This should be done after the trial
2. Homepage guidelines (i.e. landing page after signup)
   * Do:
     * Make it easy for the user to find the Databricks connection and/or access their data in Databricks
       * Set Databricks as the first/default data source/destination for the trial session
     * (Optional) Have a brief tutorial on how to use your product with the newly created connection to Databricks
   * Don’t:
     * Ask user to configure/update the Databricks connection created by Partner Connect
       * The connection should be ready to use without further input. Partner Connect also provides other useful information such as cloud, region and catalogs.

3. Environment setup guidelines
   * If you need time to set up/provision an environment for the trial after signup, please minimize the setup time. Try to keep the wait time within 5 minutes.
     * Consider using a warm pool to reduce the wait time if possible
