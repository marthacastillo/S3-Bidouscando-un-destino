## CREATING A PULL REQUEST

- This guide is created if you do not have write acces to a project.

### Fork the project:

Do a fork is get a remote personal copy of a project. 

Go to main page of the repo you want to colaborate with, and click the Fork button in the top-right corner. 


### Clone the fork of the project locally:

Do a clone is create a local copy. 

open a terminal:

```
git clone git@github.com/user/project.git

cd project
```

### Create a branch and move to it:

`git checkout -b my_branch`

### Make changes to improve the code:

Before make any change, be sure you are in the branch you created. 

`....making changes to the code...`

 Make commit:

``` git add file
    git commit -m "message"
```

### Push the branch to our GitHub fork:

`git push origin my_branch` 

To confirm your changes are available in the github fork, go to the github.com/your_user/project in the branch my_branch. 

Here, you should see the changes you made before and that mean your remote copy is updated. 

### Open a Pull Request on GitHub:

Go to the github.com/your_user/project in the branch my_branch and click the button "New Pull Request".

Then, you will see a window where you can give a title and description to the Pull Request. This is important because let to the original owner of the project determine what you were trying to do, whether your proposed changes are correct, and whether accepting the changes would improve the original project.

And hit the Create pull request button on this screen. 


 
## If you have write acces to the project you do not need to do the fork to create a PR:

Just clone the project and follow the instructions of this guide. 











