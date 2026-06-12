package main

import (
	"fmt"
	"html/template"
	"net/http"
	"os"
)

type Config struct {
	APIUrl string
}

func main() {
	http.HandleFunc("/admin", func(w http.ResponseWriter, r *http.Request) {
		apiURL := os.Getenv("NODE_API_URL")
		if apiURL == "" {
			apiURL = "http://localhost:3000"
		}

		tmpl := "<!DOCTYPE html>\n" +
			"<html>\n" +
			"<head>\n" +
			"	<meta charset=\"UTF-8\">\n" +
			"	<title>SmartCheck Admin</title>\n" +
			"	<style>\n" +
			"		body { font-family: 'Segoe UI', sans-serif; background: #f0f2f5; margin: 0; padding: 20px; }\n" +
			"		.container { max-width: 1100px; margin: 0 auto; background: white; padding: 20px; border-radius: 15px; box-shadow: 0 5px 15px rgba(0,0,0,0.1); }\n" +
			"		h1 { text-align: center; color: #333; }\n" +
			"		.tabs { display: flex; gap: 10px; margin-bottom: 20px; justify-content: center; }\n" +
			"		.tab-btn { padding: 10px 20px; cursor: pointer; border: none; background: #ddd; border-radius: 5px; font-weight: bold; }\n" +
			"		.tab-btn.active { background: #007bff; color: white; }\n" +
			"		.tab-content { display: none; }\n" +
			"		.tab-content.active { display: block; }\n" +
			"		table { width: 100%; border-collapse: collapse; margin-top: 15px; }\n" +
			"		th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }\n" +
			"		th { background: #f8f9fa; }\n" +
			"		.form-group { background: #f9f9f9; padding: 15px; border-radius: 10px; margin-bottom: 20px; border: 1px solid #eee; display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 10px; }\n" +
			"		input, select { padding: 8px; border: 1px solid #ccc; border-radius: 4px; }\n" +
			"		.btn { padding: 8px 12px; border: none; border-radius: 4px; cursor: pointer; color: white; }\n" +
			"		.btn-add { background: #28a745; }\n" +
			"		.btn-del { background: #dc3545; }\n" +
			"		.auth-box { background: #fff3cd; padding: 10px; border-radius: 5px; margin-bottom: 15px; text-align: center; border: 1px solid #ffeeba; }\n" +
			"	</style>\n" +
			"</head>\n" +
			"<body>\n" +
			"\n" +
			"<div class=\"container\">\n" +
			"	<h1>⚙️ SmartCheck Admin Panel</h1>\n" +
			"\n" +
			"	<div class=\"auth-box\">\n" +
			"		<label><strong>🔑 Токен Администратора:</strong> </label>\n" +
			"		<input type=\"text\" id=\"admin-token\" placeholder=\"Вставь сюда JWT токен (роль 4)...\" style=\"width: 60%; margin-left: 10px; display: inline-block; margin-bottom: 0;\">\n" +
			"	</div>\n" +
			"\n" +
			"	<div class=\"tabs\">\n" +
			"		<button class=\"tab-btn active\" onclick=\"openTab('users-tab', this)\">Пользователи</button>\n" +
			"		<button class=\"tab-btn\" onclick=\"openTab('lessons-tab', this)\">Пары/Расписание</button>\n" +
			"	</div>\n" +
			"\n" +
			"	\n" +
			"	<div id=\"users-tab\" class=\"tab-content active\">\n" +
			"		<div class=\"form-group\">\n" +
			"			<input id=\"u-name\" placeholder=\"ФИО\">\n" +
			"			<input id=\"u-email\" placeholder=\"Email\">\n" +
			"			<select id=\"u-role\">\n" +
			"				<option value=\"1\">Студент</option>\n" +
			"				<option value=\"2\">Преподаватель</option>\n" +
			"				<option value=\"4\">Администрация</option>\n" +
			"			</select>\n" +
			"			<input id=\"u-pass\" placeholder=\"Пароль\">\n" +
			"			<button class=\"btn btn-add\" onclick=\"addUser()\">Добавить пользователя</button>\n" +
			"		</div>\n" +
			"		<table>\n" +
			"			<thead>\n" +
			"				<tr><th>ID</th><th>Имя</th><th>Email</th><th>Роль</th><th>Действие</th></tr>\n" +
			"			</thead>\n" +
			"			<tbody id=\"user-table\"></tbody>\n" +
			"		</table>\n" +
			"	</div>\n" +
			"\n" +
			"	\n" +
			"	<div id=\"lessons-tab\" class=\"tab-content\">\n" +
			"	    <div class=\"form-group\">\n" +
			"        	<input id=\"filter-subject\" placeholder=\"Поиск предмета\" oninput=\"applyLessonFilters()\">\n" +
			"        	<input id=\"filter-group\" placeholder=\"Поиск группы\" oninput=\"applyLessonFilters()\">\n" +
			"        	<input id=\"filter-teacher\" placeholder=\"Поиск преподавателя\" oninput=\"applyLessonFilters()\">\n" +
			"        	<input id=\"filter-room\" placeholder=\"Аудитория\" oninput=\"applyLessonFilters()\">\n" +
			"        	<select id=\"filter-status\" onchange=\"applyLessonFilters()\">\n" +
			"        		<option value=\"\">Все статусы</option>\n" +
			"        		<option value=\"Идёт сейчас\">Идёт сейчас</option>\n" +
			"        		<option value=\"Будет позже\">Будет позже</option>\n" +
			"        		<option value=\"Закончилась\">Закончилась</option>\n" +
			"        	</select>\n" +
			"        </div>\n" +
			"\n" +
			"		<div class=\"form-group\">\n" +
			"			<select id=\"l-teacher\"></select>\n" +
			"			<select id=\"l-group\"></select>\n" +
			"			<input id=\"l-subject\" placeholder=\"Предмет\">\n" +
			"			<input id=\"l-start\" type=\"datetime-local\">\n" +
			"			<input id=\"l-end\" type=\"datetime-local\">\n" +
			"			<input id=\"l-room\" placeholder=\"Аудитория\">\n" +
			"			<button class=\"btn btn-add\" onclick=\"addLesson()\">Добавить пару</button>\n" +
			"		</div>\n" +
			"		<table>\n" +
			"			<thead>\n" +
			"				<tr><th>ID</th><th>Преподаватель</th><th>Группа</th><th>Предмет</th><th>Время</th><th>Каб</th><th>Статус</th><th>Действие</th></tr>\n" +
			"			</thead>\n" +
			"			<tbody id=\"lesson-table\"></tbody>\n" +
			"		</table>\n" +
			"	</div>\n" +
			"</div>\n" +
			"\n" +
			"<script>\n" +
			"	const API = '{{.APIUrl}}';\n" +
			"\n" +
			"	function getAuthHeaders() {\n" +
			"		let token = document.getElementById('admin-token').value.trim();\n" +
			"		token = token.replace(/^[\"']|[\"']$/g, '');\n" +
			"		if (token.toLowerCase().startsWith('bearer ')) {\n" +
			"			token = token.slice(7).trim();\n" +
			"		}\n" +
			"		return {\n" +
			"			'Content-Type': 'application/json',\n" +
			"			'Authorization': 'Bearer ' + token\n" +
			"		};\n" +
			"	}\n" +
			"\n" +
			"	function openTab(tabId, btn) {\n" +
			"		document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));\n" +
			"		document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));\n" +
			"		document.getElementById(tabId).classList.add('active');\n" +
			"		btn.classList.add('active');\n" +
			"	}\n" +
			"\n" +
			"	async function loadUsers() {\n" +
			"    	try {\n" +
			"    		const res = await fetch(API + '/admin/users', { headers: getAuthHeaders() });\n" +
			"    		if (!res.ok) return;\n" +
			"    		const users = await res.json();\n" +
			"    		const table = document.getElementById('user-table');\n" +
			"    		table.innerHTML = '';\n" +
			"\n" +
			"    		users.forEach(u => {\n" +
			"    			let role = 'Студент';\n" +
			"    			if (u.role_id == 2) role = 'Препод';\n" +
			"    			if (u.role_id == 4) role = 'Администрация';\n" +
			"\n" +
			"    			table.innerHTML += '<tr>' +\n" +
			"    				'<td>' + u.user_id + '</td>' +\n" +
			"    				'<td>' + u.full_name + '</td>' +\n" +
			"    				'<td>' + u.email + '</td>' +\n" +
			"    				'<td>' + role + '</td>' +\n" +
			"    				'<td><button class=\"btn btn-del\" onclick=\"deleteUser(' + u.user_id + ')\">Удалить</button></td>' +\n" +
			"    			'</tr>';\n" +
			"    		});\n" +
			"    	} catch (e) { console.error(e); }\n" +
			"    }\n" +
			"\n" +
			"	async function addUser() {\n" +
			"		const data = {\n" +
			"			full_name: document.getElementById('u-name').value.trim(),\n" +
			"			email: document.getElementById('u-email').value.trim(),\n" +
			"			password_hash: document.getElementById('u-pass').value.trim(),\n" +
			"			role_id: parseInt(document.getElementById('u-role').value)\n" +
			"		};\n" +
			"\n" +
			"		try {\n" +
			"			const res = await fetch(API + '/admin/users', {\n" +
			"				method: 'POST',\n" +
			"				headers: getAuthHeaders(),\n" +
			"				body: JSON.stringify(data)\n" +
			"			});\n" +
			"\n" +
			"			if (res.ok) {\n" +
			"				alert('Пользователь добавлен');\n" +
			"				loadUsers();\n" +
			"			} else {\n" +
			"				const err = await res.json();\n" +
			"				alert('Ошибка: ' + (err.error || 'нет доступа'));\n" +
			"			}\n" +
			"		} catch (e) { console.error(e); }\n" +
			"	}\n" +
			"\n" +
			"	async function deleteUser(id) {\n" +
			"		if (!confirm('Удалить пользователя?')) return;\n" +
			"		try {\n" +
			"			const res = await fetch(API + '/admin/users/' + id, { \n" +
			"				method: 'DELETE',\n" +
			"				headers: getAuthHeaders()\n" +
			"			});\n" +
			"			if (res.ok) loadUsers();\n" +
			"		} catch (e) { console.error(e); }\n" +
			"	}\n" +
			"\n" +
			"	let allLessons = [];\n" +
			"\n" +
			"    async function loadLessons() {\n" +
			"    	try {\n" +
			"    		const res = await fetch(API + '/admin/lessons', { headers: getAuthHeaders() });\n" +
			"    		if (!res.ok) return;\n" +
			"    		allLessons = await res.json();\n" +
			"    		renderLessons(allLessons);\n" +
			"    	} catch (e) { console.error(e); }\n" +
			"    }\n" +
			"\n" +
			"    function renderLessons(lessons) {\n" +
			"        const table = document.getElementById('lesson-table');\n" +
			"        table.innerHTML = '';\n" +
			"        const now = new Date();\n" +
			"\n" +
			"        lessons.forEach(l => {\n" +
			"            const start = l.start_time ? new Date(l.start_time) : null;\n" +
			"            const end   = l.end_time   ? new Date(l.end_time)   : null;\n" +
			"            let status = '—';\n" +
			"\n" +
			"            if (start && end) {\n" +
			"                if (now >= start && now <= end) status = 'Идёт сейчас';\n" +
			"                else if (now > end) status = 'Закончилась';\n" +
			"                else status = 'Будет позже';\n" +
			"            }\n" +
			"\n" +
			"            const timeStr = start ? start.toLocaleString('ru-RU', {\n" +
			"                    day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit'\n" +
			"                  }) + ' – ' + (end ? end.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }) : '?') : '—';\n" +
			"\n" +
			"            table.innerHTML += '<tr>' +\n" +
			"                    '<td>' + l.lesson_id + '</td>' +\n" +
			"                    '<td>' + (l.teacher_name || '—') + '</td>' +\n" +
			"                    '<td>' + (l.group_name || '—') + '</td>' +\n" +
			"                    '<td>' + (l.subject || '—') + '</td>' +\n" +
			"                    '<td>' + timeStr + '</td>' +\n" +
			"                    '<td>' + (l.room || '—') + '</td>' +\n" +
			"                    '<td>' + status + '</td>' +\n" +
			"                    '<td><button class=\"btn btn-del\" onclick=\"deleteLesson(' + l.lesson_id + ')\">Удалить</button></td>' +\n" +
			"                '</tr>';\n" +
			"        });\n" +
			"    }\n" +
			"\n" +
			"	function applyLessonFilters() {\n" +
			"    	const subject = document.getElementById('filter-subject').value.toLowerCase();\n" +
			"    	const group = document.getElementById('filter-group').value.toLowerCase();\n" +
			"    	const teacher = document.getElementById('filter-teacher').value.toLowerCase();\n" +
			"    	const room = document.getElementById('filter-room').value.toLowerCase();\n" +
			"    	const status = document.getElementById('filter-status').value;\n" +
			"    	const now = new Date();\n" +
			"\n" +
			"    	const filtered = allLessons.filter(l => {\n" +
			"    		const start = l.start_time ? new Date(l.start_time) : null;\n" +
			"    		const end = l.end_time ? new Date(l.end_time) : null;\n" +
			"    		let lessonStatus = '';\n" +
			"\n" +
			"    		if (start && end) {\n" +
			"    			if (now >= start && now <= end) lessonStatus = 'Идёт сейчас';\n" +
			"    			else if (now > end) lessonStatus = 'Закончилась';\n" +
			"    			else lessonStatus = 'Будет позже';\n" +
			"    		}\n" +
			"\n" +
			"    		return (!subject || (l.subject || '').toLowerCase().includes(subject))\n" +
			"    			&& (!group || (l.group_name || '').toLowerCase().includes(group))\n" +
			"    			&& (!teacher || (l.teacher_name || '').toLowerCase().includes(teacher))\n" +
			"    			&& (!room || (l.room || '').toLowerCase().includes(room))\n" +
			"    			&& (!status || lessonStatus === status);\n" +
			"    	});\n" +
			"    	renderLessons(filtered);\n" +
			"    }\n" +
			"\n" +
			"	async function addLesson() {\n" +
			"        const startInput = document.getElementById('l-start').value;\n" +
			"        const endInput = document.getElementById('l-end').value;\n" +
			"        const offset = -new Date().getTimezoneOffset() / 60;\n" +
			"\n" +
			"        if (!startInput || !endInput) {\n" +
			"            alert('Укажите время');\n" +
			"            return;\n" +
			"        }\n" +
			"\n" +
			"        const data = {\n" +
			"            teacher_id: parseInt(document.getElementById('l-teacher').value),\n" +
			"            group_id: parseInt(document.getElementById('l-group').value),\n" +
			"            subject: document.getElementById('l-subject').value.trim(),\n" +
			"            start_time: startInput,\n" +
			"            end_time: endInput,\n" +
			"            room: document.getElementById('l-room').value.trim(),\n" +
			"            timezone_offset_hours: offset\n" +
			"        };\n" +
			"\n" +
			"        try {\n" +
			"            const res = await fetch(API + '/admin/lessons', {\n" +
			"                method: 'POST',\n" +
			"                headers: getAuthHeaders(),\n" +
			"                body: JSON.stringify(data)\n" +
			"            });\n" +
			"\n" +
			"            if (res.ok) {\n" +
			"                alert('Пара добавлена');\n" +
			"                loadLessons();\n" +
			"            } else {\n" +
			"                const err = await res.json();\n" +
			"                alert('Ошибка: ' + (err.error || 'неизвестная ошибка'));\n" +
			"            }\n" +
			"        } catch (e) {\n" +
			"            console.error(e);\n" +
			"            alert('Ошибка сети');\n" +
			"        }\n" +
			"    }\n" +
			"\n" +
			"	async function deleteLesson(id) {\n" +
			"		if (!confirm('Удалить пару?')) return;\n" +
			"		try {\n" +
			"			const res = await fetch(API + '/admin/lessons/' + id, { \n" +
			"				method: 'DELETE',\n" +
			"				headers: getAuthHeaders()\n" +
			"			});\n" +
			"			if (res.ok) loadLessons();\n" +
			"		} catch (e) { console.error(e); }\n" +
			"	}\n" +
			"\n" +
			"	async function loadDropdowns() {\n" +
			"		try {\n" +
			"			const responses = await Promise.all([\n" +
			"				fetch(API + '/admin/teachers', { headers: getAuthHeaders() }),\n" +
			"				fetch(API + '/admin/groups', { headers: getAuthHeaders() })\n" +
			"			]);\n" +
			"\n" +
			"			if(!responses[0].ok || !responses[1].ok) return;\n" +
			"\n" +
			"			const teachers = await responses[0].json();\n" +
			"			const groups = await responses[1].json();\n" +
			"			const tSelect = document.getElementById('l-teacher');\n" +
			"			const gSelect = document.getElementById('l-group');\n" +
			"\n" +
			"			tSelect.innerHTML = '';\n" +
			"			gSelect.innerHTML = '';\n" +
			"\n" +
			"			teachers.forEach(t => {\n" +
			"				tSelect.innerHTML += '<option value=\"' + t.user_id + '\">' + t.full_name + '</option>';\n" +
			"			});\n" +
			"			groups.forEach(g => {\n" +
			"				gSelect.innerHTML += '<option value=\"' + g.group_id + '\">' + g.group_name + '</option>';\n" +
			"			});\n" +
			"		} catch (e) { console.error(e); }\n" +
			"	}\n" +
			"\n" +
			"	document.getElementById('admin-token').addEventListener('input', () => {\n" +
			"		loadUsers();\n" +
			"		loadLessons();\n" +
			"		loadDropdowns();\n" +
			"	});\n" +
			"\n" +
			"	loadUsers();\n" +
			"	loadLessons();\n" +
			"	loadDropdowns();\n" +
			"</script>\n" +
			"</body>\n" +
			"</html>"

		t, err := template.New("admin").Parse(tmpl)
		if err != nil {
			http.Error(w, err.Error(), 500)
			return
		}

		t.Execute(w, Config{APIUrl: apiURL})
	})

	fmt.Println("Admin server started at :8080")
	err := http.ListenAndServe(":8080", nil)
	if err != nil {
		panic(err)
	}
}