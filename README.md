# TODO API
This project demonstrates the connectivity of akka http with Mysql using slick.

## Start Services with SBT
- sbt clean compile
- Run Driver object under src/main/scala/org/knoldus/main

## Test with SBT
- sbt test

## Running Api's
You can use postman for running the api's
- localhost:8080/
    * POST - api/task - create task
    * GET - api/task - Get All Task
    * GET - api/task/X - Get task by ID
    * GET - api/task?id=X - same as above
    * GET - api/task?completed=true - Get completed task
    * GET - api/task?completed=false - Get Pending Task
    * GET - api/task?limit=2&skip=10
    * PUT - api/task/X?completed=true - Update status as completed
    * PUT - api/task/X?completed=true - Update status as uncompleted
    * PUT - api/task/X - Update task by ID
    * PUT - api/task?id=X - Same as above
    * DELETE - api/task/X - Delete task by ID
    * DELETE - api/task?id=X - Same as above
    * DELETE - api/task - Delete All Task