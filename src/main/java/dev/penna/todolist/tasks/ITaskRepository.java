package dev.penna.todolist.tasks;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.penna.todolist.user.UserModel;
import java.util.List;


public interface ITaskRepository extends JpaRepository<TaskModel, UUID>{
  List<TaskModel> findByIdUser(UUID idUser);
}
