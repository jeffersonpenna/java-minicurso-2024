package dev.penna.todolist.tasks;

import org.springframework.web.bind.annotation.RestController;

import dev.penna.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;





@RestController
@RequestMapping("/tasks")
public class TaskController {
  
  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    var idUser = (UUID) request.getAttribute("idUser");
    taskModel.setIdUser(idUser);

    var currentDate = LocalDateTime.now();

    if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("A data de início e termino devem ser maior que a data atual");
    }

    if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("A data de início deve ser menor que a data der termino");
    }


    var task = this.taskRepository.save(taskModel);
    return ResponseEntity.status(HttpStatus.OK).body(task);
  }

  @GetMapping("/")
  public ResponseEntity list(HttpServletRequest request) {
    var idUser = (UUID) request.getAttribute("idUser");
    var tasks = this.taskRepository.findByIdUser(idUser);
    return ResponseEntity.status(HttpStatus.OK).body(tasks);
  }

  @PutMapping("/{id}")
  public ResponseEntity update(@PathVariable UUID id, @RequestBody TaskModel taskModel, HttpServletRequest request) {
    var task = this.taskRepository.findById(id).orElse(null);

    if(task == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada");
    }

    var idUser = request.getAttribute("idUser");

    if (!task.getIdUser().equals(idUser)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão para alterar essa tarefa");
    }

    Utils.copyNonNullProperties(taskModel, task);
    var taskUpdated = this.taskRepository.save(task);

    return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);
  }
  
}
