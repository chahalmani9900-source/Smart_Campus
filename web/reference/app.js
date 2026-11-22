const SEED_DATA = {
  students: [
    { id: "STU101", name: "Aarav Sharma", email: "aarav.sharma@university.edu", dept: "Computer Science", sem: 3, gpa: 8.95, phone: "9876543210" },
    { id: "STU102", name: "Diya Nair", email: "diya.nair@university.edu", dept: "Computer Science", sem: 3, gpa: 9.20, phone: "9876543211" },
    { id: "STU103", name: "Rohan Verma", email: "rohan.verma@university.edu", dept: "Electronics", sem: 3, gpa: 8.15, phone: "9876543212" },
    { id: "STU104", name: "Ananya Iyer", email: "ananya.iyer@university.edu", dept: "Information Technology", sem: 3, gpa: 9.40, phone: "9876543213" },
    { id: "STU105", name: "Kabir Mehta", email: "kabir.mehta@university.edu", dept: "Mechanical", sem: 5, gpa: 7.85, phone: "9876543214" }
  ],
  faculty: [
    { id: "FAC201", name: "Dr. Meera Kulkarni", email: "meera.k@university.edu", dept: "Computer Science", desig: "Associate Professor", spec: "OOP & Java" },
    { id: "FAC202", name: "Prof. Rajeshwari Rao", email: "rajeshwari.r@university.edu", dept: "Computer Science", desig: "Assistant Professor", spec: "DBMS" },
    { id: "FAC203", name: "Dr. Vikramaditya Sen", email: "vikram.s@university.edu", dept: "Electronics", desig: "Professor", spec: "Embedded Systems" },
    { id: "FAC204", name: "Prof. Neha Deshmukh", email: "neha.d@university.edu", dept: "Information Technology", desig: "Assistant Professor", spec: "Data Structures" }
  ],
  courses: [
    { code: "25CSE0202", name: "Programming in Java", dept: "Computer Science", credits: 4, facultyId: "FAC201" },
    { code: "25CSE0204", name: "Database Management Systems", dept: "Computer Science", credits: 4, facultyId: "FAC202" },
    { code: "25ECE0201", name: "Digital Logic Design", dept: "Electronics", credits: 3, facultyId: "FAC203" },
    { code: "25ITE0203", name: "Data Structures in Practice", dept: "Information Technology", credits: 4, facultyId: "FAC204" }
  ],
  attendance: [
    { studentId: "STU101", courseCode: "25CSE0202", date: "2026-11-10", status: "PRESENT" },
    { studentId: "STU102", courseCode: "25CSE0202", date: "2026-11-10", status: "PRESENT" },
    { studentId: "STU103", courseCode: "25ECE0201", date: "2026-11-10", status: "ABSENT" },
    { studentId: "STU104", courseCode: "25ITE0203", date: "2026-11-11", status: "PRESENT" }
  ],
  events: [
    { id: "EVT301", name: "Annual Tech Symposium - Innovate 2026", date: "2026-11-25", venue: "Main Auditorium", org: "CSE Dept", desc: "Coding hackathon and project showcase." },
    { id: "EVT302", name: "Database & JDBC Workshop", date: "2026-12-02", venue: "Computer Lab 3", org: "ACM Chapter", desc: "Hands-on JDBC & MySQL workshop." },
    { id: "EVT303", name: "Winter Cultural & Sports Fest", date: "2026-12-15", venue: "Sports Complex", org: "Student Council", desc: "Inter-departmental sports and cultural fest." }
  ],
  activities: ["System initialized with seed academic records."]
};
let state = JSON.parse(localStorage.getItem("smartCampusState")) || structuredClone(SEED_DATA);
function saveState(logMsg) {
  if (logMsg) state.activities.unshift(`${new Date().toLocaleTimeString()} — ${logMsg}`);
  localStorage.setItem("smartCampusState", JSON.stringify(state));
  renderAll();
}
function resetDemoData() { state = structuredClone(SEED_DATA); saveState("Reset portal to default seed records."); }
document.querySelectorAll(".nav-item").forEach(btn => {
  btn.addEventListener("click", () => {
    document.querySelectorAll(".nav-item").forEach(b => b.classList.remove("active"));
    document.querySelectorAll(".tab-panel").forEach(p => p.classList.remove("active"));
    btn.classList.add("active");
    document.getElementById(`tab-${btn.dataset.tab}`).classList.add("active");
    document.getElementById("pageTitle").textContent = btn.textContent;
  });
});
function renderAll() { renderDashboard(); renderStudents(); renderFaculty(); renderCourses(); renderAttendance(); renderEvents(); renderReports(); }
function renderDashboard() {
  document.getElementById("kpiStudents").textContent = state.students.length;
  document.getElementById("kpiFaculty").textContent = state.faculty.length;
  document.getElementById("kpiCourses").textContent = state.courses.length;
  const present = state.attendance.filter(a => a.status === "PRESENT").length;
  const pct = state.attendance.length ? Math.round((present / state.attendance.length) * 100) : 0;
  document.getElementById("kpiAttendance").textContent = `${pct}%`;
  document.getElementById("dashboardEventsList").innerHTML = state.events.map(e => `<div class="event-mini"><strong>${e.name}</strong><div style="font-size:12px;color:#475569;margin-top:4px;">${e.date} • ${e.venue} (${e.org})</div></div>`).join("");
  document.getElementById("activityLog").innerHTML = state.activities.slice(0, 6).map(a => `<li>${a}</li>`).join("");
}
function renderStudents() {
  const q = (document.getElementById("studentSearch").value || "").toLowerCase();
  const filtered = state.students.filter(s => s.id.toLowerCase().includes(q) || s.name.toLowerCase().includes(q) || s.dept.toLowerCase().includes(q));
  document.getElementById("studentsTableBody").innerHTML = filtered.map(s => `<tr><td><strong>${s.id}</strong></td><td>${s.name}</td><td>${s.email}</td><td>${s.dept}</td><td>${s.sem}</td><td>${Number(s.gpa).toFixed(2)}</td><td>${s.phone}</td><td><button class="btn btn-edit" onclick="openStudentModal('${s.id}')">Edit</button><button class="btn btn-danger" onclick="deleteStudent('${s.id}')">Delete</button></td></tr>`).join("");
}
function openStudentModal(editId = null) {
  const existing = editId ? state.students.find(s => s.id === editId) : null;
  document.getElementById("modalTitle").textContent = existing ? `Edit Student (${existing.id})` : "Add New Student";
  document.getElementById("modalBody").innerHTML = `<form class="form-grid" onsubmit="saveStudent(event, '${editId || ""}')"><label>Student ID <input id="mStuId" value="${existing ? existing.id : ""}" ${existing ? "readonly" : "required"} placeholder="STU106" /></label><label>Full Name <input id="mStuName" value="${existing ? existing.name : ""}" required /></label><label>Email <input type="email" id="mStuEmail" value="${existing ? existing.email : ""}" required /></label><label>Department <input id="mStuDept" value="${existing ? existing.dept : "Computer Science"}" required /></label><label>Semester (1-8) <input type="number" min="1" max="8" id="mStuSem" value="${existing ? existing.sem : 3}" required /></label><label>GPA (0-10) <input type="number" step="0.01" min="0" max="10" id="mStuGpa" value="${existing ? existing.gpa : 8.5}" required /></label><label>Phone (10 digits) <input pattern="\d{10}" id="mStuPhone" value="${existing ? existing.phone : ""}" required /></label><button type="submit" class="btn btn-primary">Save Student</button></form>`;
  document.getElementById("modalBackdrop").classList.remove("hidden");
}
function saveStudent(e, editId) {
  e.preventDefault();
  const id = document.getElementById("mStuId").value.trim().toUpperCase();
  const student = { id, name: document.getElementById("mStuName").value.trim(), email: document.getElementById("mStuEmail").value.trim(), dept: document.getElementById("mStuDept").value.trim(), sem: Number(document.getElementById("mStuSem").value), gpa: Number(document.getElementById("mStuGpa").value), phone: document.getElementById("mStuPhone").value.trim() };
  if (editId) { const idx = state.students.findIndex(s => s.id === editId); state.students[idx] = student; saveState(`Updated student ${id}`); }
  else { if (state.students.some(s => s.id === id)) { alert("Duplicate Student ID!"); return; } state.students.push(student); saveState(`Added new student ${id} (${student.name})`); }
  closeModal();
}
function deleteStudent(id) { state.students = state.students.filter(s => s.id !== id); saveState(`Deleted student ${id}`); }
function renderFaculty() { document.getElementById("facultyTableBody").innerHTML = state.faculty.map(f => `<tr><td><strong>${f.id}</strong></td><td>${f.name}</td><td>${f.email}</td><td>${f.dept}</td><td>${f.desig}</td><td>${f.spec}</td><td><button class="btn btn-danger" onclick="deleteFaculty('${f.id}')">Delete</button></td></tr>`).join(""); }
function openFacultyModal() {
  document.getElementById("modalTitle").textContent = "Add Faculty Member";
  document.getElementById("modalBody").innerHTML = `<form class="form-grid" onsubmit="saveFaculty(event)"><label>Faculty ID <input id="mFacId" required placeholder="FAC205" /></label><label>Full Name <input id="mFacName" required /></label><label>Email <input type="email" id="mFacEmail" required /></label><label>Department <input id="mFacDept" required /></label><label>Designation <input id="mFacDesig" required /></label><label>Specialization <input id="mFacSpec" required /></label><button type="submit" class="btn btn-primary">Add Faculty</button></form>`;
  document.getElementById("modalBackdrop").classList.remove("hidden");
}
function saveFaculty(e) { e.preventDefault(); const id = document.getElementById("mFacId").value.trim().toUpperCase(); if (state.faculty.some(f => f.id === id)) return alert("Duplicate Faculty ID!"); state.faculty.push({ id, name: document.getElementById("mFacName").value.trim(), email: document.getElementById("mFacEmail").value.trim(), dept: document.getElementById("mFacDept").value.trim(), desig: document.getElementById("mFacDesig").value.trim(), spec: document.getElementById("mFacSpec").value.trim() }); saveState(`Added faculty ${id}`); closeModal(); }
function deleteFaculty(id) { state.faculty = state.faculty.filter(f => f.id !== id); saveState(`Removed faculty ${id}`); }
function renderCourses() { document.getElementById("coursesTableBody").innerHTML = state.courses.map(c => `<tr><td><strong>${c.code}</strong></td><td>${c.name}</td><td>${c.dept}</td><td>${c.credits}</td><td>${c.facultyId}</td><td><button class="btn btn-danger" onclick="deleteCourse('${c.code}')">Delete</button></td></tr>`).join(""); }
function openCourseModal() {
  document.getElementById("modalTitle").textContent = "Add Course";
  document.getElementById("modalBody").innerHTML = `<form class="form-grid" onsubmit="saveCourse(event)"><label>Course Code <input id="mCrsCode" required placeholder="25CSE0209" /></label><label>Course Name <input id="mCrsName" required /></label><label>Department <input id="mCrsDept" required /></label><label>Credits <input type="number" min="1" max="6" id="mCrsCred" value="4" required /></label><label>Faculty ID <input id="mCrsFac" required placeholder="FAC201" /></label><button type="submit" class="btn btn-primary">Save Course</button></form>`;
  document.getElementById("modalBackdrop").classList.remove("hidden");
}
function saveCourse(e) { e.preventDefault(); const code = document.getElementById("mCrsCode").value.trim().toUpperCase(); state.courses.push({ code, name: document.getElementById("mCrsName").value.trim(), dept: document.getElementById("mCrsDept").value.trim(), credits: Number(document.getElementById("mCrsCred").value), facultyId: document.getElementById("mCrsFac").value.trim().toUpperCase() }); saveState(`Added course ${code}`); closeModal(); }
function deleteCourse(code) { state.courses = state.courses.filter(c => c.code !== code); saveState(`Deleted course ${code}`); }
function renderAttendance() {
  document.getElementById("attStudentSelect").innerHTML = state.students.map(s => `<option value="${s.id}">${s.id} - ${s.name}</option>`).join("");
  document.getElementById("attCourseSelect").innerHTML = state.courses.map(c => `<option value="${c.code}">${c.code} - ${c.name}</option>`).join("");
  document.getElementById("attDate").valueAsDate = new Date();
  document.getElementById("attendanceTableBody").innerHTML = state.attendance.map(a => `<tr><td>${a.studentId}</td><td>${a.courseCode}</td><td>${a.date}</td><td class="${a.status === "PRESENT" ? "status-present" : "status-absent"}">${a.status}</td></tr>`).join("");
}
function submitAttendance(e) { e.preventDefault(); const studentId = document.getElementById("attStudentSelect").value; const courseCode = document.getElementById("attCourseSelect").value; const date = document.getElementById("attDate").value; const status = document.getElementById("attStatus").value; if (state.attendance.some(a => a.studentId === studentId && a.courseCode === courseCode && a.date === date)) { alert("Attendance already recorded!"); return; } state.attendance.unshift({ studentId, courseCode, date, status }); saveState(`Marked ${studentId} ${status} in ${courseCode}`); }
function renderEvents() { document.getElementById("eventsGrid").innerHTML = state.events.map(ev => `<div class="event-card"><h5>${ev.name}</h5><p style="font-size:13px;color:#2563eb;margin-bottom:8px;">${ev.date} • ${ev.venue}</p><p style="font-size:13px;color:#334155;margin-bottom:10px;">${ev.desc}</p><small style="color:#64748b;">Organizer: ${ev.org}</small></div>`).join(""); }
function openEventModal() {
  document.getElementById("modalTitle").textContent = "Schedule Campus Event";
  document.getElementById("modalBody").innerHTML = `<form class="form-grid" onsubmit="saveEvent(event)"><label>Event Name <input id="mEvName" required /></label><label>Date <input type="date" id="mEvDate" required /></label><label>Venue <input id="mEvVenue" required /></label><label>Organizer <input id="mEvOrg" required /></label><label>Description <textarea id="mEvDesc" required></textarea></label><button type="submit" class="btn btn-primary">Publish Event</button></form>`;
  document.getElementById("modalBackdrop").classList.remove("hidden");
}
function saveEvent(e) { e.preventDefault(); const name = document.getElementById("mEvName").value.trim(); state.events.push({ id: "EVT" + Math.floor(400 + Math.random() * 500), name, date: document.getElementById("mEvDate").value, venue: document.getElementById("mEvVenue").value.trim(), org: document.getElementById("mEvOrg").value.trim(), desc: document.getElementById("mEvDesc").value.trim() }); saveState(`Scheduled event: ${name}`); closeModal(); }
function renderReports() {
  const counts = {}; state.students.forEach(s => { counts[s.dept] = (counts[s.dept] || 0) + 1; }); const max = Math.max(...Object.values(counts), 1);
  document.getElementById("deptChartContainer").innerHTML = Object.entries(counts).map(([dept, count]) => `<div class="bar-row"><div style="display:flex;justify-content:space-between;font-size:13px;"><strong>${dept}</strong><span>${count} Students</span></div><div class="bar-track"><div class="bar-fill" style="width:${(count / max) * 100}%"></div></div></div>`).join("");
  const honors = state.students.filter(s => s.gpa >= 8.5).sort((a, b) => b.gpa - a.gpa);
  document.getElementById("honorsTableBody").innerHTML = honors.map(s => `<tr><td>${s.id}</td><td>${s.name}</td><td>${s.dept}</td><td><strong>${Number(s.gpa).toFixed(2)}</strong></td></tr>`).join("");
}
function closeModal() { document.getElementById("modalBackdrop").classList.add("hidden"); }
renderAll();
