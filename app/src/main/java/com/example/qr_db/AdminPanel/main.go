package main

import (
	"fmt"
	"html/template"
	"net/http"
)

func main() {
	http.HandleFunc("/admin", func(w http.ResponseWriter, r *http.Request) {
		tmpl := `<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>SmartCheck Admin</title>
    <style>
       body { font-family: 'Segoe UI', sans-serif; background: #f0f2f5; margin: 0; padding: 20px; }
       .container { max-width: 1100px; margin: 0 auto; background: white; padding: 20px; border-radius: 15px; box-shadow: 0 5px 15px rgba(0,0,0,0.1); }
       h1 { text-align: center; color: #333; }
       .tabs { display: flex; gap: 10px; margin-bottom: 20px; justify-content: center; }
       .tab-btn { padding: 10px 20px; cursor: pointer; border: none; background: #ddd; border-radius: 5px; font-weight: bold; }
       .tab-btn.active { background: #007bff; color: white; }
       .tab-content { display: none; }
       .tab-content.active { display: block; }
       table { width: 100%; border-collapse: collapse; margin-top: 15px; }
       th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }
       th { background: #f8f9fa; }
       .form-group { background: #f9f9f9; padding: 15px; border-radius: 10px; margin-bottom: 20px; border: 1px solid #eee; display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 10px; }
       input, select { padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
       .btn { padding: 8px 12px; border: none; border-radius: 4px; cursor: pointer; color: white; }
       .btn-add { background: #28a745; }
       .btn-del { background: #dc3545; }
    </style>
</head>
<body>
<div class="container">
    <h1>⚙️ SmartCheck Admin Panel</h1>
    <div class="tabs">
       <button class="tab-btn active" onclick="openTab('users-tab', this)">Пользователи</button>
       <button class="tab-btn" onclick="openTab('lessons-tab', this)">Пары/Расписание</button>
    </div>

    <!-- USERS -->
    <div id="users-tab" class="tab-content active">
       <div class="form-group">
          <input id="u-name" placeholder="ФИО">
          <input id="u-email" placeholder="Email">
          <select id="u-role">
             <option value="1">Студент</option>
             <option value="2">Преподаватель</option>
             <option value="3">Админ</option>
          </select>
          <select id="u-group"><option value="">Без группы</option></select>
          <input id="u-pass" placeholder="Пароль" type="password">
          <button class="btn btn-add" onclick="addUser()">Добавить пользователя</button>
       </div>
       <div class="form-group">
          <input id="g-name" placeholder="Новая группа (например ИС-22-1)">
          <button class="btn btn-add" onclick="addGroup()">Добавить группу</button>
       </div>
       <table>
          <thead><tr><th>ID</th><th>Имя</th><th>Email</th><th>Роль</th><th>Действие</th></tr></thead>
          <tbody id="user-table"></tbody>
       </table>
    </div>

    <!-- LESSONS -->
    <div id="lessons-tab" class="tab-content">
        <div class="form-group">
            <input id="filter-subject" placeholder="Поиск предмета" oninput="applyLessonFilters()">
            <input id="filter-group" placeholder="Поиск группы" oninput="applyLessonFilters()">
            <input id="filter-teacher" placeholder="Поиск преподавателя" oninput="applyLessonFilters()">
            <input id="filter-room" placeholder="Аудитория" oninput="applyLessonFilters()">
            <select id="filter-status" onchange="applyLessonFilters()">
               <option value="">Все статусы</option>
               <option value="Идёт сейчас">Идёт сейчас</option>
               <option value="Будет позже">Будет позже</option>
               <option value="Закончилась">Закончилась</option>
            </select>
        </div>
       <div class="form-group">
          <select id="l-teacher"></select>
          <select id="l-group"></select>
          <input id="l-subject" placeholder="Предмет">
          <input id="l-start" type="datetime-local">
          <input id="l-end" type="datetime-local">
          <input id="l-room" placeholder="Аудитория">
          <select id="l-type">
              <option value="normal">Обычная пара</option>
              <option value="practice">Практика</option>
          </select>
          <button class="btn btn-add" onclick="addLesson()">Добавить пару</button>
       </div>
       <table>
          <thead><tr><th>ID</th><th>Преподаватель</th><th>Группа</th><th>Предмет</th><th>Время</th><th>Каб</th><th>Статус</th><th>Действие</th></tr></thead>
          <tbody id="lesson-table"></tbody>
       </table>
    </div>
</div>

<script>
    const API = 'http://smartcheck.aspc.kz/:3000';

    function openTab(tabId, btn) {
       var tabs = document.querySelectorAll('.tab-content');
       for (var i = 0; i < tabs.length; i++) { tabs[i].classList.remove('active'); }
       var btns = document.querySelectorAll('.tab-btn');
       for (var i = 0; i < btns.length; i++) { btns[i].classList.remove('active'); }
       document.getElementById(tabId).classList.add('active');
       btn.classList.add('active');
    }

    async function loadUsers() {
        try {
           var res = await fetch(API + '/admin/users');
           var users = await res.json();
           var table = document.getElementById('user-table');
           table.innerHTML = '';
           users.forEach(function(u) {
              var role = u.role_id == 2 ? 'Препод' : (u.role_id == 3 ? 'Админ' : 'Студент');
              table.innerHTML += '<tr><td>' + u.user_id + '</td><td>' + u.full_name + '</td><td>' + u.email + '</td><td>' + role + '</td><td><button class="btn btn-del" onclick="deleteUser(' + u.user_id + ')">Удалить</button></td></tr>';
           });
        } catch (e) { console.error(e); }
    }

    async function addUser() {
       var data = {
            full_name: document.getElementById('u-name').value.trim(),
            email: document.getElementById('u-email').value.trim(),
            password_hash: document.getElementById('u-pass').value.trim(),
            role_id: parseInt(document.getElementById('u-role').value),
            group_id: document.getElementById('u-group').value ? parseInt(document.getElementById('u-group').value) : null
        };
       try {
          var res = await fetch(API + '/admin/users', {
             method: 'POST',
             headers: { 'Content-Type': 'application/json' },
             body: JSON.stringify(data)
          });
          if (res.ok) { alert('Пользователь добавлен'); loadUsers(); } else { alert('Ошибка'); }
       } catch (e) { console.error(e); }
    }

    async function addGroup() {
        var name = document.getElementById('g-name').value.trim();
        if(!name) return alert('Введите название группы');
        try {
            var res = await fetch(API + '/admin/groups', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ group_name: name })
            });
            if (res.ok) { alert('Группа добавлена'); loadDropdowns(); }
        } catch (e) { console.error(e); }
    }

    async function deleteUser(id) {
       if (!confirm('Удалить пользователя?')) return;
       try {
          await fetch(API + '/admin/users/' + id, { method: 'DELETE' });
          loadUsers();
       } catch (e) { console.error(e); }
    }

    var allLessons = [];
    async function loadLessons() {
        try {
           var res = await fetch(API + '/admin/lessons');
           allLessons = await res.json();
           renderLessons(allLessons);
        } catch (e) { console.error(e); }
    }

    function renderLessons(lessons) {
        var table = document.getElementById('lesson-table');
        table.innerHTML = '';
        var now = new Date();
        lessons.forEach(function(l) {
            var start = l.start_time ? new Date(l.start_time) : null;
            var end = l.end_time ? new Date(l.end_time) : null;
            var status = '—';
            if (start && end) {
                if (now >= start && now <= end) status = 'Идёт сейчас';
                else if (now > end) status = 'Закончилась';
                else status = 'Будет позже';
            }
            var timeStr = start ? start.toLocaleString('ru-RU', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' }) + ' – ' + (end ? end.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }) : '?') : '—';
            table.innerHTML += '<tr><td>' + l.lesson_id + '</td><td>' + (l.teacher_name || '—') + '</td><td>' + (l.group_name || '—') + '</td><td>' + (l.subject || '—') + '</td><td>' + timeStr + '</td><td>' + (l.room || '—') + '</td><td>' + status + '</td><td><button class="btn btn-del" onclick="deleteLesson(' + l.lesson_id + ')">Удалить</button></td></tr>';
        });
    }

    function applyLessonFilters() {
        var subject = document.getElementById('filter-subject').value.toLowerCase();
        var group = document.getElementById('filter-group').value.toLowerCase();
        var teacher = document.getElementById('filter-teacher').value.toLowerCase();
        var room = document.getElementById('filter-room').value.toLowerCase();
        var status = document.getElementById('filter-status').value;
        var now = new Date();

        var filtered = allLessons.filter(function(l) {
           var start = l.start_time ? new Date(l.start_time) : null;
           var end = l.end_time ? new Date(l.end_time) : null;
           var lessonStatus = (start && end) ? (now >= start && now <= end ? 'Идёт сейчас' : (now > end ? 'Закончилась' : 'Будет позже')) : '';
           return (!subject || (l.subject || '').toLowerCase().includes(subject)) &&
                  (!group || (l.group_name || '').toLowerCase().includes(group)) &&
                  (!teacher || (l.teacher_name || '').toLowerCase().includes(teacher)) &&
                  (!room || (l.room || '').toLowerCase().includes(room)) &&
                  (!status || lessonStatus === status);
        });
        renderLessons(filtered);
    }

    async function addLesson() {
        var startInput = document.getElementById('l-start').value;
        var endInput = document.getElementById('l-end').value;
        var offset = -new Date().getTimezoneOffset() / 60;
        if (!startInput || !endInput) return alert('Укажите время');
        var data = {
            teacher_id: parseInt(document.getElementById('l-teacher').value),
            group_id: parseInt(document.getElementById('l-group').value),
            subject: document.getElementById('l-subject').value.trim(),
            start_time: startInput,
            end_time: endInput,
            room: document.getElementById('l-room').value.trim(),

            lesson_type:
                document.getElementById('l-type').value,

            timezone_offset_hours: offset
        };
        try {
            var res = await fetch(API + '/admin/lessons', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data) });
            if (res.ok) { alert('Пара добавлена'); loadLessons(); } else { var err = await res.json(); alert('Ошибка: ' + (err.error || 'неизвестная ошибка')); }
        } catch (e) { alert('Ошибка сети'); }
    }

    async function deleteLesson(id) {
       if (!confirm('Удалить пару?')) return;
       try {
          await fetch(API + '/admin/lessons/' + id, { method: 'DELETE' });
          loadLessons();
       } catch (e) { console.error(e); }
    }

    async function loadDropdowns() {
       try {
          var resT = await fetch(API + '/admin/teachers');
          var resG = await fetch(API + '/admin/groups');
          var teachers = await resT.json();
          var groups = await resG.json();

          var uGroup = document.getElementById('u-group');
          var lTeacher = document.getElementById('l-teacher');
          var lGroup = document.getElementById('l-group');

          uGroup.innerHTML = '<option value="">Без группы</option>';
          lTeacher.innerHTML = '';
          lGroup.innerHTML = '';

          groups.forEach(function(g) {
             var opt = '<option value="' + g.group_id + '">' + g.group_name + '</option>';
             uGroup.innerHTML += opt;
             lGroup.innerHTML += opt;
          });
          teachers.forEach(function(t) {
             lTeacher.innerHTML += '<option value="' + t.user_id + '">' + t.full_name + '</option>';
          });
       } catch (e) { console.error(e); }
    }

    loadUsers();
    loadLessons();
    loadDropdowns();
</script>
</body>
</html>`

		t, err := template.New("admin").Parse(tmpl)
		if err != nil {
			http.Error(w, err.Error(), 500)
			return
		}
		t.Execute(w, nil)
	})

	fmt.Println("Admin server started at :8080")
	if err := http.ListenAndServe(":8080", nil); err != nil {
		panic(err)
	}
}