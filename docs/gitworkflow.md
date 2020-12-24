# Git workflow

## Gitflow


[Gitworkflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)


## Common Git commands

* Get a copy of a repository

```bash
git clone <repository URL>
```

* Commit changes (1)

```bash
git commit -m 'Short, imperative comment' <file name(s)>
```

* Commit changes (2)

```bash
git add <file name(s)>
git commit -m 'Short, imperative comment'
```

* Create a new (feature) branch (off the current branch)

```bash
git checkout -b <initials/feature/name_of_feature>
```

* Initial push of a new branch to the remote repository

```bash
git push -u origin <initials/feature/name_of_feature>
```

* Subsequent pushes to the remote repository

```bash
git push
```

* Move to an existing branch

```bash
git checkout <name of branch>
```

* Merge changes from the `master` branch of the remote repository into the a feature branch.  This should **always** be done prior to a **push** to pick up new commits

```bash
git checkout master
git pull
git checkout <initials/feature/name_of_feature>
git merge master
```
