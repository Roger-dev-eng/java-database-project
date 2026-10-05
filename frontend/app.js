const schemas = {
  jogos: { title: "Jogos", id: "id_jogo", columns: ["id_jogo", "nome", "ano_lancamento", "desenvolvedora", "genero"], fields: [["nome", "Nome", "text"], ["ano_lancamento", "Ano", "number"], ["desenvolvedora", "Desenvolvedora", "text"], ["genero", "Gênero", "text"]] },
  jogadores: { title: "Jogadores", id: "id_jogador", columns: ["id_jogador", "nickname", "email", "fk_jogo"], fields: [["nickname", "Nickname", "text"], ["email", "E-mail", "email"], ["fk_jogo", "Jogo associado", "number"]] },
  plataformas: { title: "Plataformas", id: "id_plataforma", columns: ["id_plataforma", "nome", "horas_jogadas", "ultima_sessao", "fk_jogador"], fields: [["nome", "Nome", "text"], ["horas_jogadas", "Horas jogadas", "number"], ["fk_jogador", "Jogador associado", "number"]] },
  avaliacoes: { title: "Avaliações", id: "id_avaliacao", columns: ["id_avaliacao", "nota", "comentario", "status", "data_avaliacao", "fk_jogador", "fk_jogo"], fields: [["nota", "Nota", "number"], ["comentario", "Comentário", "text"], ["status", "Status", "text"], ["data_avaliacao", "Data", "date"], ["fk_jogador", "Jogador", "number"], ["fk_jogo", "Jogo", "number"]] }
};
const labels = { id_jogo: "ID", id_jogador: "ID", id_plataforma: "ID", id_avaliacao: "ID", nome: "Nome", nickname: "Nickname", email: "E-mail", ano_lancamento: "Ano", desenvolvedora: "Desenvolvedora", genero: "Gênero", horas_jogadas: "Horas", ultima_sessao: "Última sessão", fk_jogo: "Jogo", fk_jogador: "Jogador", nota: "Nota", comentario: "Comentário", status: "Status", data_avaliacao: "Data" };
const element = (selector) => document.querySelector(selector);
const escapeHtml = (value) => String(value ?? "").replace(/[&<>'"]/g, (char) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", "'": "&#39;", '"': "&quot;" }[char]));

async function api(path, options) {
  document.body.classList.add("is-loading");
  try {
    let response;
    try {
      response = await fetch(`/api/${path}`, options || {});
    } catch {
      throw new Error("O backend Java não está respondendo. Inicie o WebServer e tente novamente.");
    }
    const raw = await response.text();
    let data;
    try {
      data = raw ? JSON.parse(raw) : {};
    } catch {
      throw new Error(`O servidor retornou uma resposta inválida (${response.status}).`);
    }
    if (!response.ok) throw new Error(data.error || "Falha na operação.");
    return data;
  } finally {
    document.body.classList.remove("is-loading");
  }
}

function showToast(message, isError) {
  const toast = element("#toast");
  toast.textContent = message;
  toast.className = isError ? "error" : "ok";
  setTimeout(() => { toast.className = ""; }, 3000);
}

function navigate(page) {
  document.querySelectorAll("#nav button").forEach((button) => button.classList.toggle("active", button.dataset.page === page));
  if (page === "home") renderHome();
  else if (page === "dashboard") renderDashboard();
  else if (page === "consultas") renderQueries();
  else renderResource(page);
}

function atomButton(label, action, kind = "secondary") {
  return `<button class="action-button ${kind}" data-action="${action}">${label}</button>`;
}

function moleculeModuleCard(key, name, description, index) {
  return `<article class="overview-card"><div class="card-index">0${index}</div><h3>${name}</h3><p>${description}</p><div class="card-actions">${atomButton("Cadastrar", `create:${key}`, "primary")} ${atomButton("Ver registros", `view:${key}`)}</div></article>`;
}

function renderHome() {
  const modules = [
    ["jogos", "Jogos", "Catálogo, gênero e desenvolvedora."],
    ["jogadores", "Jogadores", "Perfis e jogos associados."],
    ["plataformas", "Plataformas", "Horas jogadas e vínculos."],
    ["avaliacoes", "Avaliações", "Notas, status e comentários."]
  ];
  element("#content").innerHTML = `<section class="overview-hero"><div><span class="eyebrow">VISÃO GERAL / MENU PRINCIPAL</span><h1>Bom te ver,<br><em>${escapeHtml(element("#user-label").textContent)}.</em></h1><p>Escolha uma ação para começar. Todos os cadastros ficam disponíveis aqui.</p></div><div class="quick-actions"><span class="eyebrow">ACESSO RÁPIDO</span>${atomButton("Abrir dashboard", "dashboard", "quick")} ${atomButton("Executar consultas", "consultas", "quick")}</div></section><section><div class="section-heading"><div><span class="eyebrow">OPERAÇÃO</span><h2>Cadastros</h2></div><p>Crie e consulte os registros do sistema.</p></div><div class="overview-grid">${modules.map((module, index) => moleculeModuleCard(module[0], module[1], module[2], index + 1)).join("")}</div></section>`;
  document.querySelectorAll("[data-action]").forEach((button) => {
    button.onclick = () => {
      const [action, resource] = button.dataset.action.split(":");
      if (action === "create") renderForm(resource, {});
      else navigate(resource || action);
    };
  });
}

async function renderResource(resource) {
  const schema = schemas[resource];
  try {
    const rows = await api(resource);
    element("#content").innerHTML = `<header class="row"><div><span class="eyebrow">MÓDULO OPERACIONAL</span><h1>${schema.title}</h1><p>${rows.length} registro(s) armazenado(s).</p></div><button id="new">+ Novo ${schema.title.slice(0, -1)}</button></header><div class="table"><table><thead><tr>${schema.columns.map((column) => `<th>${labels[column] || column}</th>`).join("")}<th>AÇÕES</th></tr></thead><tbody>${rows.map((row) => `<tr>${schema.columns.map((column) => `<td>${escapeHtml(row[column])}</td>`).join("")}<td><button class="edit" data-id="${row[schema.id]}">Editar</button><button class="del" data-id="${row[schema.id]}">Excluir</button></td></tr>`).join("") || "<tr><td>Nenhum registro.</td></tr>"}</tbody></table></div>`;
    element("#new").onclick = () => renderForm(resource, {});
    document.querySelectorAll(".edit").forEach((button) => { button.onclick = () => renderForm(resource, rows.find((row) => String(row[schema.id]) === button.dataset.id)); });
    document.querySelectorAll(".del").forEach((button) => { button.onclick = () => deleteResource(resource, button.dataset.id); });
  } catch (error) { renderError(error); }
}

function renderForm(resource, row) {
  const schema = schemas[resource];
  const fields = schema.fields.map(([name, label, type]) => `<label>${label}<input name="${name}" type="${type}" value="${escapeHtml(row[name])}" required></label>`).join("");
  element("#content").innerHTML = `<header class="row"><div><span class="eyebrow">${row[schema.id] ? "EDITAR" : "NOVO"} REGISTRO</span><h1>${row[schema.id] ? "Editar" : "Novo"} ${schema.title}</h1></div><button class="back" id="back">← Voltar</button></header><form id="record" class="form">${fields}<button type="submit">Salvar registro →</button></form>`;
  element("#back").onclick = () => renderResource(resource);
  element("#record").onsubmit = async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(event.target));
    try {
      await api(row[schema.id] ? `${resource}/${row[schema.id]}` : resource, { method: row[schema.id] ? "PUT" : "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload) });
      showToast("Registro salvo.");
      renderResource(resource);
    } catch (error) { showToast(error.message, true); }
  };
}

async function deleteResource(resource, id) {
  if (!confirm("Excluir este registro?")) return;
  try { await api(`${resource}/${id}`, { method: "DELETE" }); showToast("Registro excluído."); renderResource(resource); }
  catch (error) { showToast(error.message, true); }
}

async function renderDashboard() {
  try {
    const data = await api("dashboard");
    const value = (key) => data[key][0] ? data[key][0].total : 0;
    element("#content").innerHTML = `<header><span class="eyebrow">DASHBOARD / AO VIVO</span><h1>Visão analítica</h1><p>Leitura rápida do movimento no banco.</p></header><div class="metrics"><div>JOGOS<strong>${value("jogos")}</strong></div><div>JOGADORES<strong>${value("jogadores")}</strong></div><div class="lime">MÉDIA DAS NOTAS<strong>${Number(value("media")).toFixed(2)}</strong></div></div><div class="charts"><section><span class="eyebrow">DISTRIBUIÇÃO</span><h2>Jogos por gênero</h2>${renderBars(data.generos)}</section><section><span class="eyebrow">RANKING</span><h2>Mais avaliados</h2>${renderBars(data.ranking)}</section></div>`;
  } catch (error) { renderError(error); }
}

function renderBars(rows) {
  const max = Math.max(...rows.map((row) => Number(row.total)), 1);
  return rows.map((row) => `<p class="bar"><span>${escapeHtml(row.nome)}</span><i style="width:${Number(row.total) / max * 100}%"></i><b>${row.total}</b></p>`).join("");
}

async function renderQueries() {
  element("#content").innerHTML = `<header><span class="eyebrow">EXPLORAÇÃO</span><h1>Consultas</h1><p>Consulte os dados operacionais.</p></header><div class="query"><select id="query-resource"><option>jogos</option><option>jogadores</option><option>plataformas</option><option>avaliacoes</option></select><input id="query-term" placeholder="Buscar por texto"><button id="run-query">Consultar →</button></div><div id="query-result"></div>`;
  element("#run-query").onclick = async () => {
    const resource = element("#query-resource").value;
    const term = element("#query-term").value.toLowerCase();
    const rows = (await api(resource)).filter((row) => !term || Object.values(row).some((value) => String(value ?? "").toLowerCase().includes(term)));
    element("#query-result").innerHTML = `<pre>${escapeHtml(JSON.stringify(rows, null, 2))}</pre>`;
  };
}

function renderError(error) { element("#content").innerHTML = `<div class="error"><h1>Não foi possível carregar.</h1><p>${escapeHtml(error.message)}</p></div>`; }

function enterApp(user) {
  element("#login").classList.add("hidden");
  element("#app").classList.remove("hidden");
  element("#user-label").textContent = user;
  element("#initial").textContent = user.charAt(0).toUpperCase();
  navigate("home");
}

element("#login-form").onsubmit = (event) => {
  event.preventDefault();
  const user = element("#user-name").value.trim();
  if (!user) return;
  localStorage.setItem("arcade-user", user);
  enterApp(user);
};
element("#logout").onclick = () => { localStorage.removeItem("arcade-user"); window.location.reload(); };
document.querySelectorAll("#nav button").forEach((button) => { button.onclick = () => navigate(button.dataset.page); });
const savedUser = localStorage.getItem("arcade-user");
if (savedUser) enterApp(savedUser);