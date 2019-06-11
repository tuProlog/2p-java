# tuProlog

## How to develop tuProlog

- The `master` branch is protected. 
Only maintainers can edit it. 
It is updated only when stable versions are released.

- The `develop` branch may be edited by all developers, but is should be edited only by merging consistent feature 
branches.
    * Actual development should occur on feature branches
    
- Feature branches should be named using the schema `feature/name-of-the-features`
    * Feature names should be dash separated
    * Notice that GitLab creates feature branches which are not preceded by the `feature/` prefix, 
    by default, when creating a Merge Request out of an issue. This is tolerated, even if discouraged
    
- Students and interns are, in general, not allowed to edit the repository directly. 
They should fork the repository instead and keep their fork aligned to the `develop` branch of this repository.
    * Forks can only be integrated by maintainers, and only after all tests are proven to pass, 
    and the students' code has been reviewed.
    
- Roughly, the protocol for developers willing to implement some feature or target some issue is as follows
    1. If there exists no issue for the feature, open an issue and assign it to your-self
        * otherwise select an unassigned, open issue and assign it to your self
        * if you want to work on an assigned issue, you must contact the current assignee __on the issue chat__ and wait 
        for him/her to provide his/her __explicit__ consent
        
    2. You will then create a feature branch and work on it. In doing so, please
        * keep your feature branch aligned to the `develop` one, by frequently merging the `develop` branch into your own
        * commit frequently, possibly letting commits be consistent, and have meaningful messages
            - if the commit is not consistent, please start the commit message with the `[wip]` prefix (work in progress)
            - if the commit is storing non-compiling code, please start the commit message with the `[wip] [BROKEN]` prefix
        * within a feature branch, tests are not required to pass for all commits
        * Notice that, in any case, the issue assignee is __responsible__ for the feature branch 
        
    3. Once you are done with a feature, you must integrate the feature within the `develop` branch. To do so:
        1. Ensure all tests are passing in the feature branch
        2. Merge the `develop` branch into your feature branch
        3. Ensure all tests are passing in the feature branch
        4. Merge the feature branch into the `develop` branch
            - You can do so by mean of a Merge Request created from the issue, if you created it 
        5. Close the issue
            - GitLab may have done automatically if you used a Merge Request
            
- If some tests are not passing on the `develop` branch when a feature branch is created, the developer which is responsible
for that branch must simply ensure that the set of failing tests is left unaffected when the feature branch is integrated
into `develop`, unless some are fixed. Of course this means the feature branch is only allowed to fix broken tests or 
leave them unaltered. 

          

## How to run tuProlog

1. Clone this repository
    
        git clone https://gitlab.com/pika-lab/tuprolog/2p  
        
2. Jump into the `2p` directory created by Git

3. Run tuProlog:

    - You can start the tuProlog IDE by running
    
    ```bash
    ./gradlew ide
    ```
    
    - You can start the tuProlog CLI by running
    
    ```bash
    ./gradlew repl -q
    ```