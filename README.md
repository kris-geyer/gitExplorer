# git Explorer
## basic outline
Git explorer is intend to offer a user-friendly method of accessing GitHub through an android smartphone. Simply, enter the name of the author of a git, explore their repositories and then select the one you wish to review. Alternatively, utilise the auto complete features if you know which repository you wish to select.
One you have selected the repository to review, a screen will open that allows you to review some details about the repository: When it was created, the number of pull requests and the number of forks.
If you want to review the repository in greater detail, there is an option to review the description of the program (the readme file) or review the files and folders in the document in a nice and intuitive fashion.

## For the developers
Git explorer operates as your run of the mill MVVM app which utilises a REST API. It employs call backs on receiving content from HTTPS calls, this notifies the appropriate class in the view model package. Some error handling has been implemented but this could be greatly improved upon if less strict time constraints were imposed.
A recycler view is utilised to display the file contents. This feature was included in spite of the brief for the app development. The brief requested utilising as few unnecessary 3rd party libraries as possible. But by including a file viewer, I could explore both the jsonReader capabilities and Moshi.
They were utilised in slightly different settings. The jsonReader was employed when there was a vast number of variables (extensive details of every repository - including a description of the owner in each repository). To save memory, the jsonReader was utilised to iterate through the json and then extract essential values. However, due to the nature of jsonReader the value had to be anticipated, for instance if a string was null then an error was thrown. Therefore, only variables in the json that were highly reliable were utilised in the app.
Moshi was employed when a high proportion of the data being utilised by the json, thereby keeping needless expenditure of memory to a minimum.


demo: https://youtu.be/utlh90PQLT8
