/* Kullanıcı dropdown’unu doldurur */
async function loadUserEmailsForDropdown() {
    try {
        const res = await fetch("/api/users");
        if (!res.ok) throw new Error();
        const users = await res.json();
        const options = `<option disabled selected value="">Choose user</option>` +
            users.map(u => `<option value="${u.userId}">${u.name} (${u.email})</option>`).join("");

        const sel1 = document.getElementById("clubOwnerSelect");
        if (sel1) sel1.innerHTML = options;

        const sel2 = document.getElementById("rentalUserSelect");
        if (sel2) sel2.innerHTML = options;

    } catch {
        const err = `<option disabled>Error loading users</option>`;
        const sel1 = document.getElementById("clubOwnerSelect");
        if (sel1) sel1.innerHTML = err;

        const sel2 = document.getElementById("rentalUserSelect");
        if (sel2) sel2.innerHTML = err;
    }
}

/* Kulüp dropdown’unu doldurur */
async function loadClubDropdown() {
    const courtSel = document.getElementById("courtClubSelect");
    const rentalSel = document.getElementById("rentalClubSelect");
    try {
        const res = await fetch("/api/clubs");
        if (!res.ok) throw new Error();
        const clubs = await res.json();
        const options = `<option disabled selected value="">Choose club</option>` +
            clubs.map(c => `<option value="${c.clubID}">${c.name}</option>`).join("");
        if (courtSel) courtSel.innerHTML = options;
        if (rentalSel) rentalSel.innerHTML = options;
    } catch {
        if (courtSel) courtSel.innerHTML = `<option disabled>Error loading clubs</option>`;
        if (rentalSel) rentalSel.innerHTML = `<option disabled>Error loading clubs</option>`;
    }
}

/* Kort dropdown’unu kulübe göre doldurur */
async function loadCourtDropdown(clubId) {
    const sel = document.getElementById("rentalCourtSelect");
    if (!sel || !clubId) return;
    try {
        const res = await fetch(`/api/clubs/${clubId}/courts`);
        if (!res.ok) throw new Error();
        const courts = await res.json();
        sel.innerHTML = `<option disabled selected value="">Choose court</option>` +
            courts.map(c => `<option value="${c.courtID}">${c.name}</option>`).join("");
    } catch {
        sel.innerHTML = `<option disabled>Error loading courts</option>`;
    }
}


/* Check Availability Dropdownları Doldurur */
async function loadAvailabilityDropdowns() {
    const clubSelect = document.getElementById("availabilityClubSelect");
    const courtSelect = document.getElementById("availabilityCourtSelect");

    try {
        const res = await fetch("/api/clubs");
        if (!res.ok) throw new Error("Clubs fetch failed");

        const clubs = await res.json();

        clubSelect.innerHTML =
            `<option disabled selected value="">Choose club</option>` +
            clubs.map(c => `<option value="${c.clubID}">${c.name}</option>`).join("");

        clubSelect.addEventListener("change", async () => {
            const clubId = clubSelect.value;
            try {
                const resCourts = await fetch(`/api/clubs/${clubId}/courts`);
                if (!resCourts.ok) throw new Error("Courts fetch failed");

                const courts = await resCourts.json();
                courtSelect.innerHTML =
                    `<option disabled selected value="">Choose court</option>` +
                    courts.map(c => `<option value="${c.courtID}">${c.name}</option>`).join("");
            } catch {
                courtSelect.innerHTML = `<option disabled>Error loading courts</option>`;
            }
        });

    } catch {
        clubSelect.innerHTML = `<option disabled>Error loading clubs</option>`;
        courtSelect.innerHTML = `<option disabled>Select club first</option>`;
    }
}
async function loadRentalDropdown() {
    const rentalSel = document.getElementById("rentalSelect");
    if (!rentalSel) return;

    try {
        const rentalsRes = await fetch("/api/rentals");
        if (!rentalsRes.ok) throw new Error();
        const rentals = await rentalsRes.json();

        // Kulüp ve kort isimlerini paralel çek
        const clubCache = {};
        const courtCache = {};

        async function getClubName(id) {
            if (clubCache[id]) return clubCache[id];
            try {
                const r = await fetch(`/api/clubs/${id}`);
                const c = await r.json();
                return (clubCache[id] = c.name || id);
            } catch { return id; }
        }
        async function getCourtName(id) {
            if (courtCache[id]) return courtCache[id];
            try {
                const r = await fetch(`/api/courts/${id}`);
                const c = await r.json();
                return (courtCache[id] = c.name || id);
            } catch { return id; }
        }

        // Her rental için görünüm metni hazırla
        const enriched = await Promise.all(
            rentals.map(async r => ({
                id:       r.rentalId,               // <-- DOĞRU alan adı
                text:     `Rental #${r.rentalId} — `
                    + `${await getClubName(r.clubId)} / ${await getCourtName(r.courtId)} — `
                    + `${r.startTime} (${r.duration}h)`
            }))
        );

        rentalSel.innerHTML =
            `<option disabled selected value="">Select Rental</option>` +
            enriched.map(e => `<option value="${e.id}">${e.text}</option>`).join("");

    } catch {
        rentalSel.innerHTML = `<option disabled>Error loading rentals</option>`;
    }
}





export function homeHandler(app) {
    app.innerHTML = `
    <h1>Welcome to the Padel Club System</h1>
    <nav>
      <a href="/" data-link>Home</a> |
      <a href="/clubs" data-link>All Clubs</a>
    </nav>

    <!-- Search Club -->
    <form id="searchForm" class="mt-4" style="max-width:400px;">
      <div class="input-group mb-3">
        <input type="text" id="searchInput" class="form-control" placeholder="Search club by name">
        <button class="btn btn-primary" type="submit">Search</button>
      </div>
    </form>
    <div id="searchResults"></div>

    <div class="d-flex gap-4 my-4">

  <!-- 👤 Add User -->
  <div class="card p-3" style="width:400px;">
    <h4>Add New User</h4>
    <input id="userName" class="form-control mb-2" placeholder="Name">
    <input id="userEmail" class="form-control mb-2" placeholder="Email" type="email">
    <button id="addUserBtn" class="btn btn-success">Add User</button>
    <div id="userAddStatus" class="mt-2"></div>
  </div>

  <!-- 📅 Check Court Availability -->
  <div class="card p-3" style="width:400px;">
    <h4>Check Court Availability</h4>
    <select id="availabilityClubSelect" class="form-select mb-2"></select>
    <select id="availabilityCourtSelect" class="form-select mb-2"></select>
    <input id="availabilityDate" class="form-control mb-2" type="date">
    <button id="checkAvailabilityBtn" class="btn btn-info">Check Availability</button>
    <div id="availabilityResults" class="mt-3"></div>
  </div>

</div>



    <!-- Add Club -->
    <div class="card p-3 my-4" style="max-width:400px;">
      <h4>Add New Club</h4>
      <input id="clubName" class="form-control mb-2" placeholder="Club Name">
      <select id="clubOwnerSelect" class="form-select mb-2"></select>
      <button id="addClubBtn" class="btn btn-primary">Add Club</button>
      <div id="clubAddStatus" class="mt-2"></div>
    </div>

    <!-- Add Court -->
    <div class="card p-3 my-4" style="max-width:400px;">
      <h4>Add New Court</h4>
      <select id="courtClubSelect" class="form-select mb-2"></select>
      <input id="courtName" class="form-control mb-2" placeholder="Court Name">
      <button id="addCourtBtn" class="btn btn-primary">Add Court</button>
      <div id="courtAddStatus" class="mt-2"></div>
    </div>

   <!-- Rental Operations Row -->
<div class="d-flex flex-wrap gap-4 my-4">
  <!-- Add Rental -->
  <div class="card p-3" style="width:400px;">
    <h4>Add New Rental</h4>
    <select id="rentalUserSelect" class="form-select mb-2"></select>
    <select id="rentalClubSelect" class="form-select mb-2"></select>
    <select id="rentalCourtSelect" class="form-select mb-2"></select>
    <input id="rentalDate" class="form-control mb-2" type="date">
    <input id="rentalHour" class="form-control mb-2" type="number" placeholder="Start hour (8-17)">
    <input id="rentalDuration" class="form-control mb-2" type="number" placeholder="Duration (1-10)">
    <button id="addRentalBtn" class="btn btn-primary">Add Rental</button>
    <div id="rentalAddStatus" class="mt-2"></div>
  </div>

  <!-- 🔧 Update or Delete Rental -->
<div class="card p-3" style="width:400px;">
  <h4>Update or Delete Rental</h4>

  <select id="rentalSelect" class="form-select mb-2">
    <option disabled selected>Choose rental to update/delete</option>
  </select>

  <input id="updateRentalDate" class="form-control mb-2" type="date" placeholder="New Date">
  <input id="updateRentalHour" class="form-control mb-2" type="number" placeholder="New Start Hour (8-17)">
  <input id="updateRentalDuration" class="form-control mb-2" type="number" placeholder="New Duration (1-10)">

  <div class="d-flex gap-2">
    <button id="updateRentalBtn" class="btn btn-warning w-100">Update</button>
    <button id="deleteRentalBtn" class="btn btn-danger w-100">Delete</button>
  </div>

  <div id="rentalUpdateStatus" class="mt-2"></div>
</div>




   
    `;

    // Event Listeners
    document.getElementById("availabilityClubSelect").addEventListener("change", e => {
        loadCourtDropdown(parseInt(e.target.value), "availabilityCourtSelect");
    });

    document.getElementById("rentalClubSelect").addEventListener("change", e => {
        loadCourtDropdown(parseInt(e.target.value), "rentalCourtSelect");
    });

    document.getElementById("checkAvailabilityBtn").addEventListener("click", async () => {
        const clubId = parseInt(document.getElementById("availabilityClubSelect").value);
        const courtId = parseInt(document.getElementById("availabilityCourtSelect").value);
        const date = document.getElementById("availabilityDate").value;
        const resultsDiv = document.getElementById("availabilityResults");

        try {
            const res = await fetch(`/api/clubs/${clubId}/courts/${courtId}/available?date=${date}`);

            if (!res.ok) throw new Error();
            const availableHours = (await res.text()).split(",").filter(Boolean);
            resultsDiv.innerHTML = availableHours.length
                ? `Available Hours: ${availableHours.join(", ")}`
                : "No hours available.";
        } catch {
            resultsDiv.textContent = "Error loading availability.";
        }
    });

    document.getElementById("searchForm").addEventListener("submit", async e => {
        e.preventDefault();
        const name = document.getElementById("searchInput").value.trim();
        const out = document.getElementById("searchResults");
        if (!name) return (out.innerHTML = `<p class="text-danger">Please enter a club name.</p>`);
        try {
            const res = await fetch(`/api/clubs/search?name=${encodeURIComponent(name)}`);
            if (!res.ok) throw new Error();
            const clubs = await res.json();
            out.innerHTML = clubs.length
                ? `<ul class="list-group">${clubs.map(c => `<li class="list-group-item"><a data-link href="/clubs/${c.clubID}">${c.name}</a></li>`).join("")}</ul>`
                : `<p>No clubs found for "<strong>${name}</strong>".</p>`;
        } catch {
            out.innerHTML = `<p class="text-danger">Error fetching clubs.</p>`;
        }
    });

    document.getElementById("addUserBtn").addEventListener("click", async () => {
        const name = document.getElementById("userName").value.trim();
        const email = document.getElementById("userEmail").value.trim();
        const msg = document.getElementById("userAddStatus");
        if (!name || !email) return (msg.textContent = "Name and email cannot be empty.");

        const res = await fetch("/api/users", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email })
        });

        if (res.ok) {
            msg.textContent = "User created!";
            document.getElementById("userName").value = "";
            document.getElementById("userEmail").value = "";
            loadUserEmailsForDropdown();
        } else {
            msg.textContent = "Error: " + (await res.text());
        }
    });

    document.getElementById("addClubBtn").addEventListener("click", async () => {
        const name = document.getElementById("clubName").value.trim();
        const owner = parseInt(document.getElementById("clubOwnerSelect").value, 10);
        const msg = document.getElementById("clubAddStatus");

        if (!name || Number.isNaN(owner)) return (msg.textContent = "Fill all fields.");

        const res = await fetch("/api/clubs", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, userID: owner })
        });

        if (res.ok) {
            msg.textContent = "Club created!";
            document.getElementById("clubName").value = "";
            document.getElementById("clubOwnerSelect").selectedIndex = 0;
            loadClubDropdown();
        } else {
            msg.textContent = "Error: " + (await res.text());
        }
    });

    document.getElementById("addCourtBtn").addEventListener("click", async () => {
        const clubSel = document.getElementById("courtClubSelect");
        const clubId = parseInt(clubSel.value, 10);
        const courtName = document.getElementById("courtName").value.trim();
        const msg = document.getElementById("courtAddStatus");

        if (!courtName || Number.isNaN(clubId))
            return (msg.textContent = "Please fill all fields.");

        const res = await fetch("/api/courts", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name: courtName, clubId })
        });

        if (res.ok) {
            msg.textContent = `Court "${courtName}" created!`;
            document.getElementById("courtName").value = "";
            clubSel.selectedIndex = 0;
        } else {
            msg.textContent = "Error: " + (await res.text());
        }
    });

    document.getElementById("rentalClubSelect").addEventListener("change", async (e) => {
        const clubId = parseInt(e.target.value, 10);
        if (!Number.isNaN(clubId)) await loadCourtDropdown(clubId);
    });
    // --- Add Rental -------------------------------------------------
    document.getElementById("addRentalBtn").addEventListener("click", async () => {
        const userId = parseInt(document.getElementById("rentalUserSelect").value, 10);
        const clubId = parseInt(document.getElementById("rentalClubSelect").value, 10);
        const courtId = parseInt(document.getElementById("rentalCourtSelect").value, 10);
        let rawDate = document.getElementById("rentalDate").value;
        const hour = parseInt(document.getElementById("rentalHour").value, 10);
        const duration = parseInt(document.getElementById("rentalDuration").value, 10);
        const msg = document.getElementById("rentalAddStatus");

        if ([userId, clubId, courtId, hour, duration].some(isNaN) || !rawDate)
            return (msg.textContent = "Fill all fields correctly.");

        if (hour < 8 || hour > 17) {
            msg.textContent = "Start hour must be between 08 and 17.";
            return;
        }

        if (rawDate.includes(".")) {
            const [day, month, year] = rawDate.split(".");
            rawDate = `${year}-${month}-${day}`;
        }

        const date = rawDate;
        const startTime = `${date}T${hour.toString().padStart(2, '0')}:00:00`;

        try {
            const availableHoursRes = await fetch(
                `/api/clubs/${clubId}/courts/${courtId}/available?date=${date}`
            );

            if (!availableHoursRes.ok) throw new Error("Failed to fetch available hours");

            const availableHoursText = await availableHoursRes.text();
            const availableHours = availableHoursText
                .split(',')
                .map(h => parseInt(h))
                .filter(h => !isNaN(h));

            const requiredHours = Array.from({ length: duration }, (_, i) => hour + i);
            const isAvailable = requiredHours.every(h => availableHours.includes(h));

            if (!isAvailable) {
                msg.textContent = "Selected time is not available. Please choose another slot.";
                return;
            }

            const res = await fetch("/api/rentals", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ userId, clubId, courtId, startTime, duration })
            });

            if (res.ok) {
                msg.textContent = "Rental created!";
                loadRentalDropdown(); // 🔄 dropdown güncelle
            } else {
                const errText = await res.text();
                msg.textContent = "Error: " + errText;
            }
        } catch (e) {
            msg.textContent = "Unexpected error: " + e.message;
        }
    });

// ---------- UPDATE  Rental ----------------------------------------
    document.getElementById("updateRentalBtn").addEventListener("click", async () => {
        const rentalId      = Number(document.getElementById("rentalSelect").value);
        const newDate       = document.getElementById("updateRentalDate").value.trim();
        const newHour       = Number(document.getElementById("updateRentalHour").value);
        const newDuration   = Number(document.getElementById("updateRentalDuration").value);
        const msg           = document.getElementById("rentalUpdateStatus");

        if (isNaN(rentalId) || !newDate || isNaN(newHour) || isNaN(newDuration)) {
            msg.textContent = "Please fill all fields correctly.";
            return;
        }

        const newStartTime = `${newDate}T${String(newHour).padStart(2, "0")}:00:00`;  // ✅ doğru format
        console.log("📦 Gönderilen startTime:", newStartTime);

        try {
            const res = await fetch(`/api/rentals/${rentalId}`, {
                method : "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ startTime: newStartTime, duration: newDuration })
            });

            if (res.ok) {
                msg.textContent = "Rental updated successfully.";
                await loadRentalDropdown();                    // ► liste tazele
                document.getElementById("rentalSelect").value = ""; // dropdown'u sıfırla
            } else {
                msg.textContent = "Error updating rental: " + (await res.text());
            }
        } catch (e) {
            msg.textContent = "Unexpected error: " + e.message;
        }
    });

// ---------- DELETE Rental -----------------------------------------
    document.getElementById("deleteRentalBtn").addEventListener("click", async () => {
        const rentalId = Number(document.getElementById("rentalSelect").value);
        const msg      = document.getElementById("rentalUpdateStatus");

        if (isNaN(rentalId)) {
            msg.textContent = "Please select a rental.";
            return;
        }

        if (!confirm("Are you sure you want to delete this rental?")) return;

        try {
            const res = await fetch(`/api/rentals/${rentalId}`, { method: "DELETE" });

            if (res.ok) {
                msg.textContent = "Rental deleted successfully.";
                await loadRentalDropdown();                    // ► liste tazele
                document.getElementById("rentalSelect").value = ""; // dropdown'u sıfırla
            } else {
                msg.textContent = "Error deleting rental: " + (await res.text());
            }
        } catch (e) {
            msg.textContent = "Unexpected error: " + e.message;
        }
    });


// Sayfa yüklendiğinde dropdown'ları güncelle
    loadUserEmailsForDropdown();
    loadClubDropdown();
    loadAvailabilityDropdowns();
    loadRentalDropdown();


}



export async function clubsListHandler(app) {
    app.innerHTML = `
    <h1>Clubs List</h1>
    <p>Loading clubs...</p>
    <a href="/" data-link>Back to Home</a>
  `;

    try {
        const response = await fetch('/api/clubs');
        const clubs = await response.json();

        const listHtml = `
      <ul>
        ${clubs.map(club => `
          <li><a href="/clubs/${club.clubID}" data-link>${club.name}</a></li>
        `).join('')}
      </ul>
    `;

        app.innerHTML = `
      <h1>Clubs List</h1>
      ${listHtml}
      <a href="/" data-link>Back to Home</a>
    `;
    } catch (e) {
        app.innerHTML = `
      <h1>Clubs List</h1>
      <p style="color:red;">Could not load clubs.</p>
      <a href="/" data-link>Back to Home</a>
    `;
    }
}

export async function clubDetailsHandler(app, clubID) {
    app.innerHTML = `<h1>Loading club details...</h1>`;

    try {
        const response = await fetch(`/api/clubs/${clubID}`);
        if (!response.ok) throw new Error("Not found");
        const club = await response.json();

        app.innerHTML = `
      <h1>Club: ${club.name}</h1>
      <p><strong>Club ID:</strong> ${club.clubID}</p>
      <p><strong>Owner UID:</strong> ${club.userID}</p>
      <br>
      <!-- Navigate to the club owner’s user page -->
      <a href="/users/${club.userID}" data-link>View User Details</a><br>
      <!-- Back links -->
      <a href="/clubs" data-link>← Back to Clubs List</a><br>
      <a href="/" data-link>Home</a><br>
      <!-- View courts under this club -->
      <a href="/clubs/${club.clubID}/courts" data-link>View Courts</a>
    `;
    } catch (e) {
        app.innerHTML = `
      <h1>Club not found</h1>
      <a href="/clubs" data-link>← Back to Clubs List</a><br>
      <a href="/" data-link>Home</a>
    `;
    }
}
export async function courtsListHandler(app, clubID) {
    app.innerHTML = `
    <h1>Courts List</h1>
    <p>Loading courts...</p>
    <a href="/" data-link>Back to Home</a>
  `;

    try {
        // Varsayım: /api/clubs/{clubID}/courts endpointi array olarak court nesneleri döner
        // Her court: { courtID: "...", name: "Court A1" }
        const response = await fetch(`/api/clubs/${clubID}/courts`);
        if (!response.ok) throw new Error();
        const courts = await response.json();

        const listHtml = `
      <ul>
        ${courts.map(court => `
          <li><a href="/clubs/${clubID}/courts/${court.courtID}" data-link>${court.name}</a></li>
        `).join('')}
      </ul>
    `;

        app.innerHTML = `
      <h1>Courts List</h1>
      ${listHtml}
      <br>
      <a href="/" data-link>Back to Home</a>
      <a href="/clubs/${clubID}" data-link>Back to Club Details</a>
    `;
    } catch (e) {
        app.innerHTML = `
      <h1>Courts List</h1>
      <p style="color:red;">Could not load courts.</p>
      <a href="/" data-link>Back to Home</a>
      <a href="/clubs/${clubID}" data-link>Back to Club Details</a>
    `;
    }
}

export async function courtDetailsHandler(app, clubID, courtID) {
    app.innerHTML = `<h1>Loading court details...</h1>`;

    try {
        const response = await fetch(`/api/courts/${courtID}`);
        if (!response.ok) throw new Error("Not found");
        const court = await response.json();

        app.innerHTML = `
      <h1>Court: ${court.name}</h1>
      <p><strong>Court ID:</strong> ${court.courtID}</p>
      <p><strong>Club ID:</strong> ${court.clubId}</p>
      <br>
      <a href="/clubs/${court.clubId}/courts" data-link>← Back to Courts List</a><br>
      <a href="/clubs/${court.clubId}" data-link>← Back to Club Details</a><br>
      <a href="/" data-link>Home</a><br>
      <a href="/clubs/${court.clubId}/courts/${court.courtID}/rentals" data-link>View Rentals</a>
    `;
    } catch (e) {
        app.innerHTML = `
      <h1>Court not found</h1>
      <a href="/clubs/${clubID}/courts" data-link>← Back to Courts List</a><br>
      <a href="/clubs/${clubID}" data-link>← Back to Club Details</a><br>
      <a href="/" data-link>Home</a>
    `;
    }
}




export async function courtRentalsListHandler(app, clubID, courtID) {
    app.innerHTML = `
      <h1>Rentals for Court</h1>
      <div class="text-muted">Loading rentals...</div>
      <a href="/clubs/${clubID}/courts/${courtID}" data-link class="btn btn-outline-secondary btn-sm">← Back to Court</a>
      <a href="/" data-link class="btn btn-outline-secondary btn-sm">Home</a>
    `;

    try {
        const response = await fetch(`/api/clubs/${clubID}/courts/${courtID}/rentals`);
        if (!response.ok) throw new Error('API failed');
        const rentals = await response.json();

        if (!rentals.length) {
            app.innerHTML = `<h3>No rentals found for this court.</h3>`;
            return;
        }

        const tableRows = rentals.map(rental => `
            <tr>
                <td>${rental.startTime}</td>
                <td>${rental.duration}</td>
                <td><a href="/users/${rental.userId}" data-link>${rental.userId}</a></td>
                <td>
                    <a href="/clubs/${rental.clubId}/courts/${rental.courtId}/rentals/${rental.rentalId}" data-link class="btn btn-sm btn-primary">Details</a>
                </td>
            </tr>
        `).join('');

        app.innerHTML = `
          <h1 class="mb-4">Rentals for Court: ${courtID}</h1>
          <table class="table table-bordered table-hover table-sm">
              <thead>
                  <tr><th>Start Time</th><th>Duration (hrs)</th><th>User ID</th><th>Actions</th></tr>
              </thead>
              <tbody>${tableRows}</tbody>
          </table>
          <a href="/clubs/${clubID}/courts/${courtID}" data-link class="btn btn-outline-secondary btn-sm">← Back to Court</a>
          <a href="/" data-link class="btn btn-outline-secondary btn-sm">Home</a>
        `;
    } catch (e) {
        app.innerHTML = `
          <h1>Error loading rentals.</h1>
          <p class="text-danger">${e.message}</p>
          <a href="/clubs/${clubID}/courts/${courtID}" data-link class="btn btn-outline-secondary btn-sm">← Back to Court</a>
          <a href="/" data-link class="btn btn-outline-secondary btn-sm">Home</a>
        `;
    }
}





export async function rentalDetailsHandler(app, clubID, courtID, rentalID) {
    app.innerHTML = `ai<h1>Loading rental detls...</h1>`;

    try {
        // 1. Rental detayını çek
        const response = await fetch(`/api/rentals/${rentalID}`);
        if (!response.ok) throw new Error("Not found");
        const rental = await response.json();

        // 2. Court ve Club isimlerini çek
        const [courtResp, clubResp] = await Promise.all([
            fetch(`/api/courts/${rental.courtId}`),
            fetch(`/api/clubs/${rental.clubId}`)
        ]);
        const court = courtResp.ok ? await courtResp.json() : {};
        const club = clubResp.ok ? await clubResp.json() : {};

        app.innerHTML = `
      <h1>Rental Details</h1>
      <h4 class="mb-3">Court: <b>${court.name ? court.name : rental.courtId}</b> &nbsp; | &nbsp; Club: <b>${club.name ? club.name : rental.clubId}</b></h4>

      <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle" style="max-width:520px;">
          <tbody>
            <tr><th scope="row">Rental ID</th><td>${rental.rentalId}</td></tr>
            <tr><th scope="row">User ID</th><td>
              <a href="/users/${rental.userId}" data-link>${rental.userId}</a>
            </td></tr>
            <tr><th scope="row">Start Time</th><td>${rental.startTime}</td></tr>
            <tr><th scope="row">Duration (hrs)</th><td>${rental.duration}</td></tr>
            <tr><th scope="row">Club ID</th><td>${rental.clubId}</td></tr>
            <tr><th scope="row">Court ID</th><td>${rental.courtId}</td></tr>
          </tbody>
        </table>
      </div>
      <a href="/users/${rental.userId}/rentals" data-link class="btn btn-outline-secondary btn-sm me-2 mt-3">← Back to User Rentals</a>
      <a href="/" data-link class="btn btn-outline-secondary btn-sm mt-3">Home</a>
    `;
    } catch (e) {
        app.innerHTML = `
      <h1>Rental not found</h1>
      <a href="/" data-link class="btn btn-outline-secondary btn-sm mt-3">Home</a>
    `;
    }
}



// USERS LIST (dummy)
export function usersListHandler(app) {
    app.innerHTML = `
    <h1>Users List</h1>
    <nav>
      <a href="/" data-link>Home</a>
    </nav>
    <ul>
      <li><a href="/users/1" data-link>User 1</a></li>
      <li><a href="/users/2" data-link>User 2</a></li>
    </ul>
  `;
}

export async function userDetailsHandler(app, userID) {
    app.innerHTML = `<h1>Loading user details...</h1>`;

    try {
        const response = await fetch(`/api/users/${userID}`);
        if (!response.ok) throw new Error("Not found");
        const user = await response.json();

        app.innerHTML = `
      <h1>User Details</h1>
      <table style="border-collapse: collapse; width: 60%; margin-top: 20px;">
        <tr><th style="border: 1px solid #ddd; padding: 10px; background-color: #f2f2f2;">User ID</th><td style="border: 1px solid #ddd; padding: 10px;">${user.userId}</td></tr>
        <tr><th style="border: 1px solid #ddd; padding: 10px; background-color: #f2f2f2;">Name</th><td style="border: 1px solid #ddd; padding: 10px;">${user.name}</td></tr>
        <tr><th style="border: 1px solid #ddd; padding: 10px; background-color: #f2f2f2;">Email</th><td style="border: 1px solid #ddd; padding: 10px;">${user.email}</td></tr>
        <tr><th style="border: 1px solid #ddd; padding: 10px; background-color: #f2f2f2;">Token</th><td style="border: 1px solid #ddd; padding: 10px;">${user.token}</td></tr>
      </table>
      <br>
      <a href="/users/${user.userId}/rentals" data-link>View Rentals</a>
      <a href="/users" data-link>Back to Users</a>
      <a href="/" data-link>Home</a>
    `;
    } catch (e) {
        app.innerHTML = `
      <h1>User not found</h1>
      <a href="/users" data-link>Back to Users</a>
      <a href="/" data-link>Home</a>
    `;
    }
}



export async function userRentalsListHandler(app, userID) {
    app.innerHTML = `
    <h1>Rentals for User</h1>
    <div>Loading rentals...</div>
    <a href="/users/${userID}" data-link>← Back to User Details</a>
    <a href="/" data-link>Home</a>
  `;

    try {
        // 1. Rentals listesini çek
        const response = await fetch(`/api/users/${userID}/rentals`);
        if (!response.ok) throw new Error();
        const rentals = await response.json();

        // 2. Kullanıcı adını çek (isteğe bağlı)
        let userName = userID;
        try {
            const userResp = await fetch(`/api/users/${userID}`);
            if (userResp.ok) {
                // Eğer user endpoint'i dizi döndürüyorsa:
                // const [user] = await userResp.json();
                // userName = user.name || userID;
                // Eğer obje döndürüyorsa:
                const user = await userResp.json();
                userName = user.name || userID;
            }
        } catch {}

        // 3. Her rental için club ve court isimlerini çek
        // Tüm club/court id'leri için paralel fetch atıyoruz (gelişmiş, ama gerçekçi çözüm)
        const clubCache = {};
        const courtCache = {};

        async function getClubName(clubId) {
            if (clubCache[clubId]) return clubCache[clubId];
            try {
                const res = await fetch(`/api/clubs/${clubId}`);
                if (!res.ok) return clubId;
                const club = await res.json();
                clubCache[clubId] = club.name || clubId;
                return clubCache[clubId];
            } catch {
                return clubId;
            }
        }

        async function getCourtName(courtId) {
            if (courtCache[courtId]) return courtCache[courtId];
            try {
                const res = await fetch(`/api/courts/${courtId}`);
                if (!res.ok) return courtId;
                const court = await res.json();
                courtCache[courtId] = court.name || courtId;
                return courtCache[courtId];
            } catch {
                return courtId;
            }
        }

        // Rentals dizisini isimlerle zenginleştiriyoruz
        const rentalsWithNames = await Promise.all(
            rentals.map(async rental => ({
                ...rental,
                clubName: await getClubName(rental.clubId),
                courtName: await getCourtName(rental.courtId),
            }))
        );

        const tableRows = rentalsWithNames.map(rental => `
     <tr>
       <td>${rental.rentalId}</td>
       <td>${rental.clubName}</td>
       <td>${rental.courtName}</td>
       <td>${rental.startTime}</td>
       <td>${rental.duration}</td>
       <td><a href="/clubs/${rental.clubId}/courts/${rental.courtId}/rentals/${rental.rentalId}" data-link>View</a></td>
     </tr>
   `).join('');


        app.innerHTML = `
      <h1>Rentals for User: ${userName}</h1>
      <table style="border-collapse: collapse; width: 80%; margin-top: 20px;">
        <thead>
          <tr>
            <th style="background-color: #f2f2f2;">Rental ID</th>
            <th style="background-color: #f2f2f2;">Club</th>
            <th style="background-color: #f2f2f2;">Court</th>
            <th style="background-color: #f2f2f2;">Start Time</th>
            <th style="background-color: #f2f2f2;">Duration (hrs)</th>
            <th style="background-color: #f2f2f2;">Details</th>
          </tr>
        </thead>
        <tbody>
          ${tableRows}
        </tbody>
      </table>
      <br>
      <a href="/users/${userID}" data-link>← Back to User Details</a>
      <a href="/" data-link>Home</a>
    `;
    } catch (e) {
        app.innerHTML = `
      <h1>Rentals for User</h1>
      <p style="color:red;">Could not load rentals.</p>
      <a href="/users/${userID}" data-link>← Back to User Details</a>
      <a href="/" data-link>Home</a>
    `;
    }
}
