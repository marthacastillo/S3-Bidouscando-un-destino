### About git book 

#### There are three stages that the files can reside in:
1. Commited: the data is safely store in your local database
2. Modified: it means that you have chenged the file but have not commited it to the databasse yet
3. Staged: It means that you have marked  a modified file in its current version to go into your next commit snapshot

#### Three main sections of a Git project:
1. The git directory: is where Git stores the metadata and object database for your project.
2. The working directory: is a checkout of one version of your project, the files are pulled out of the compressed database in the Gt directory and placed on disk for you to use or modifiy
3. The staging area: it is a file, generally contained in your Git directory, that
stores information about what will go into your next commit. It’s sometimes re-
ferred to as the “index”, but it’s also common to refer to it as the staging area.

#### The basic Git workflow goes something like this:
1. You modify files in your working directory.
2. You stage the files, adding snapshots of them to your staging area.
3. You do a commit, which takes the files as they are in the staging area and
stores that snapshot permanently to your Git directory.

If a particular version of a file is in the Git directory, it’s considered commit-
ted. If it has been modified and was added to the staging area, it is staged. And
if it was changed since it was checked out but has not been staged, it is modi-
fied.In Chapter 2, you’ll learn more about these states and how you can either
take advantage of them or skip the staged part entirely.

Each file in your working directory can be in one of two
states: tracked or untracked. 

#### .gitignore file

Here is another example .gitignore file:
```
# no .a files
*.a
# but do track lib.a, even though you're ignoring .a files above
!lib.a
# only ignore the TODO file in the current directory, not subdir/TODO
/TODO
# ignore all files in the build/ directory
build/
# ignore doc/notes.txt, but not doc/server/arch.txt
doc/*.txt
# ignore all .pdf files in the doc/ directory
doc/**/*.pdf
```
- GitHub maintains a fairly comprehensive list of good .gitignore file ex-
amples for dozens of projects and languages at https://github.com/github/
gitignore if you want a starting point for your project.
