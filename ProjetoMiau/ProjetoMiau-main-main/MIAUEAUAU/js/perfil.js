document.addEventListener("DOMContentLoaded", () => {
  
  const fb = document.createElement('div');
  fb.id = 'feedbackContainer';
  Object.assign(fb.style, {
    position: 'fixed',
    top: '10%',
    left: '50%',
    transform: 'translateX(-50%)',
    padding: '1rem 2rem',
    backgroundColor: 'rgba(0,0,0,0.75)',
    color: '#fff',
    borderRadius: '.5rem',
    fontSize: '1rem',
    zIndex: 1000,
    display: 'none'
  });
  document.body.appendChild(fb);
  const showFeedback = (msg, d = 1500) => {
    fb.textContent = msg;
    fb.style.display = 'block';
    setTimeout(() => fb.style.display = 'none', d);
  };

  
  const nameEl = document.getElementById('userNameDisplay');
  if (nameEl) nameEl.textContent = localStorage.getItem('nome') || 'Cliente';

  
  const tt = document.getElementById('toggleSenha'),
        pw = document.getElementById('senha');
  if (tt && pw) tt.addEventListener('click', () => {
    const is = pw.type === 'password';
    pw.type = is ? 'text' : 'password';
    tt.textContent = is ? 'Ocultar' : 'Mostrar';
  });

  
  document.querySelectorAll('.tab-button').forEach(tab => {
    tab.addEventListener('click', () => {
      document.querySelectorAll('.tab-button').forEach(t => t.classList.remove('active'));
      document.querySelectorAll('.section').forEach(s => s.classList.add('hidden'));
      tab.classList.add('active');
      document.getElementById(tab.dataset.target).classList.remove('hidden');
    });
  });
  if (document.querySelector('.tab-button')) document.querySelector('.tab-button').click();

  
  const cpf = document.getElementById('cpf');
  if (cpf) cpf.addEventListener('input', e => {
    let v = e.target.value.replace(/\D/g, '').slice(0, 11);
    v = v.replace(/(\d{3})(\d)/, '$1.$2')
         .replace(/(\d{3})(\d)/, '$1.$2')
         .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    e.target.value = v;
  });

  
  const profileImage   = document.querySelector('.profile-picture'),
        fileInput      = document.getElementById('fileInput'),
        editPhotoBtn   = document.querySelector('.edit-photo-button'),
        deletePhotoBtn = document.querySelector('.delete-photo-button');
  if (editPhotoBtn && fileInput) {
    editPhotoBtn.addEventListener('click', () => fileInput.click());
    fileInput.addEventListener('change', () => {
      const f = fileInput.files[0];
      if (!f) return;
      const r = new FileReader();
      showFeedback('Carregando foto de perfil...');
      r.onload = e => {
        profileImage.src = e.target.result;
        localStorage.setItem('profilePhoto', e.target.result);
        showFeedback('Foto de perfil atualizada!');
      };
      r.readAsDataURL(f);
    });
  }
  if (deletePhotoBtn) {
    deletePhotoBtn.addEventListener('click', () => {
      if (confirm("Tem certeza que deseja apagar a foto de perfil?")) {
        profileImage.src = 'placeholder-profile.png';
        localStorage.removeItem('profilePhoto');
        showFeedback('Foto de perfil apagada!');
      }
    });
  }

  
  const formProfile = document.getElementById('profileForm');
  if (formProfile) {
    formProfile.addEventListener('submit', e => {
      e.preventDefault();
      const fields = ['nome','sobrenome','email','senha','cpf'],
            vals   = fields.map(id => formProfile[id].value.trim()),
            emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (vals.some(v => !v))       { showFeedback('Preencha todos os campos.'); return; }
      if (!emailRe.test(vals[2]))   { showFeedback('E-mail inválido.');     return; }
      if (vals[3].length < 4)       { showFeedback('Senha muito curta.');     return; }
      fields.forEach((id,i) => localStorage.setItem(id, vals[i]));
      showFeedback('Dados salvos com sucesso!');
    });
    ['nome','sobrenome','email','senha','cpf'].forEach(id => {
      const el = document.getElementById(id), v = localStorage.getItem(id);
      if (el && v) el.value = v;
    });
    const sp = localStorage.getItem('profilePhoto');
    if (sp && profileImage) profileImage.src = sp;
  }

  
  const newPetForm    = document.getElementById('newPetForm'),
        petNameInput  = document.getElementById('newPetName'),
        petPhotoInput = document.getElementById('newPetPhoto'),
        petTypeInput  = document.getElementById('newPetType'),
        petAgeInput   = document.getElementById('newPetAge'),
        petBreedInput = document.getElementById('newPetBreed'),
        petSizeInput  = document.getElementById('newPetSize'),
        petsContainer = document.getElementById('petsContainer'),
        petTemplate   = document.getElementById('pet-template');

  
  let btnCadastrar = newPetForm.querySelector('.btn-cadastrar');
  if (!btnCadastrar) {
    btnCadastrar = document.createElement('button');
    btnCadastrar.type = 'submit';
    btnCadastrar.className = 'btn-cadastrar';
    btnCadastrar.innerHTML = '<i class="fas fa-plus-circle"></i> Cadastrar PET';
    newPetForm.appendChild(btnCadastrar);
  }

  let meusPets = JSON.parse(localStorage.getItem('meusPets') || '[]');

  function renderPets() {
    petsContainer.innerHTML = '';
    if (meusPets.length === 0) {
      petsContainer.innerHTML = `
        <div class="empty-state">
          Você ainda não cadastrou nenhum PET.
          <br/>
          Use o formulário acima para começar!
        </div>`;
      return;
    }
    meusPets.forEach(p => {
      const clone = petTemplate.content.cloneNode(true),
            card  = clone.querySelector('.appointment-card');

      clone.querySelector('.avatar').src          = p.photo || 'francisco.jpeg';
      clone.querySelector('.pet-name').textContent   = p.name;
      clone.querySelector('.pet-type').textContent   = p.type;
      clone.querySelector('.pet-age').textContent    = p.age;
      clone.querySelector('.pet-breed').textContent  = p.breed;
      clone.querySelector('.pet-size').textContent   = p.size;

      card.querySelector('.cancel-card-btn').addEventListener('click', () => {
        meusPets = meusPets.filter(x => x.id !== p.id);
        localStorage.setItem('meusPets', JSON.stringify(meusPets));
        showFeedback('PET apagado com sucesso.');
        renderPets();
        populatePetSelect();
      });
      petsContainer.appendChild(clone);
    });
  }

  function populatePetSelect() {
    const petSelect = document.getElementById('petSelect');
    petSelect.innerHTML = '';
    if (meusPets.length) {
      petSelect.append(new Option('Selecione seu PET',''));
      meusPets.forEach(p => petSelect.append(new Option(p.name,p.id)));
      petSelect.disabled = false;
    } else {
      petSelect.append(new Option('Nenhum PET cadastrado',''));
      petSelect.disabled = true;
    }
  }

  renderPets();
  populatePetSelect();

  newPetForm.addEventListener('submit', e => {
    e.preventDefault();
    const nome  = petNameInput.value.trim(),
          type  = petTypeInput.value,
          age   = petAgeInput.value,
          breed = petBreedInput.value.trim(),
          size  = petSizeInput.value,
          id    = Date.now().toString(),
          photo = petPhotoInput.files[0]
                    ? URL.createObjectURL(petPhotoInput.files[0])
                    : null;

    if (!nome||!type||!age||!breed||!size) {
      showFeedback('Preencha todos os campos do PET.');
      return;
    }

    meusPets.push({ id, name: nome, photo, type, age, breed, size });
    localStorage.setItem('meusPets', JSON.stringify(meusPets));

    newPetForm.reset();
    renderPets();
    populatePetSelect();
    showFeedback('PET cadastrado com sucesso!');
  });

  
  const formConsultas     = document.getElementById('newConsultaForm'),
        inputDatetime     = document.getElementById('newConsultaDatetime'),
        doctorSelect      = document.getElementById('doctorSelect'),
        petSelectDOM      = document.getElementById('petSelect'),
        cardsContainerEl  = document.getElementById('cardsContainer'),
        templateCons      = document.getElementById('appointment-template');
  let consultas = JSON.parse(localStorage.getItem('consultas') || '[]');

  function renderConsultas() {
    cardsContainerEl.innerHTML = '';
    const nowB = new Date(new Date().toLocaleString('en-US',{ timeZone:'America/Sao_Paulo' }));
    consultas.sort((a,b)=>new Date(a.datetime)-new Date(b.datetime))
             .forEach(c => {
      const clone     = templateCons.content.cloneNode(true),
            card      = clone.querySelector('.appointment-card'),
            btnCancel = clone.querySelector('.cancel-card-btn'),
            statusSpan= clone.querySelector('.status'),
            dt        = new Date(c.datetime);

      clone.querySelector('.avatar').src            = c.avatarUrl;
      clone.querySelector('.doctor-name').textContent      = c.doctorName;
      clone.querySelector('.doctor-specialty').textContent = c.specialty;
      clone.querySelector('.card-date').textContent        = dt.toLocaleDateString();
      clone.querySelector('.card-time').textContent        = dt.toLocaleTimeString([], {hour:'2-digit',minute:'2-digit'});
      clone.querySelector('.card-pet-name').textContent    = c.petName;
      clone.querySelector('.card-type').textContent        = c.type;

      if (dt < nowB) {
        card.classList.add('rejected');
        statusSpan.textContent = 'Reprovada';
        statusSpan.classList.replace('approved','rejected');
      }

      btnCancel.addEventListener('click', e => {
        e.stopPropagation();
        if (confirm("Tem certeza que deseja cancelar a consulta?")) {
          consultas = consultas.filter(x=>x.id!==c.id);
          localStorage.setItem('consultas', JSON.stringify(consultas));
          showFeedback('Consulta cancelada com sucesso!');
          renderConsultas();
        }
      });

      cardsContainerEl.appendChild(clone);
    });
  }

  renderConsultas();

  formConsultas.addEventListener('submit', e => {
    e.preventDefault();
    const dtVal   = inputDatetime.value,
          docOpt  = doctorSelect.options[doctorSelect.selectedIndex],
          petId   = petSelectDOM.value,
          petObj  = meusPets.find(p=>p.id===petId) || {};
    if (!dtVal||!docOpt.value||!petId) {
      showFeedback('Preencha data, médico e selecione um PET.');
      return;
    }
    const nova = {
      id: Date.now(),
      datetime:    dtVal,
      doctorName:  docOpt.dataset.name,
      specialty:   docOpt.dataset.specialty,
      avatarUrl:   docOpt.dataset.avatar,
      petName:     petObj.name,
      type:        'Presencial'
    };
    consultas.push(nova);
    localStorage.setItem('consultas', JSON.stringify(consultas));
    formConsultas.reset();
    doctorSelect.selectedIndex = 0;
    petSelectDOM.selectedIndex = 0;
    renderConsultas();
    showFeedback('Consulta marcada com sucesso!');
  });
});
