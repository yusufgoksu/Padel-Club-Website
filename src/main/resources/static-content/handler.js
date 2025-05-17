// HOME
export function homeHandler(app) {
    app.innerHTML = `
    <h1>Welcome to the Padel Club System</h1>
    <nav>
      <a href="/" data-link>Home</a> |
      <a href="/clubs" data-link>Clubs</a>
    </nav>
    <p>Welcome!</p>
  `;
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
    <a href="/clubs/${clubID}/courts/${courtID}" data-link class="btn btn-outline-secondary btn-sm me-2 mt-3">← Back to Court</a>
    <a href="/" data-link class="btn btn-outline-secondary btn-sm mt-3">Home</a>
  `;

    try {
        const response = await fetch(`/api/clubs/${clubID}/courts/${courtID}/rentals`);
        if (!response.ok) throw new Error();
        const rentals = await response.json();

        const tableRows = rentals.map(rental => `
      <tr>
        <td>${rental.startTime}</td>
        <td>${rental.duration}</td>
        <td>
          <a href="/users/${rental.userId}" data-link>${rental.userId}</a>
        </td>
        <td>
          <a href="/clubs/${rental.clubId}/courts/${rental.courtId}/rentals/${rental.rentalID}" data-link class="btn btn-sm btn-primary">Details</a>
        </td>
      </tr>
    `).join('');

        app.innerHTML = `
      <h1 class="mb-4">Rentals for Court: ${courtID}</h1>
      <div class="table-responsive">
        <table class="table table-bordered table-hover table-sm align-middle">
          <thead class="table-light">
            <tr>
              <th>Start Time</th>
              <th>Duration (hrs)</th>
              <th>User ID</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            ${tableRows}
          </tbody>
        </table>
      </div>
      <a href="/clubs/${clubID}/courts/${courtID}" data-link class="btn btn-outline-secondary btn-sm me-2 mt-3">← Back to Court</a>
      <a href="/" data-link class="btn btn-outline-secondary btn-sm mt-3">Home</a>
    `;
    } catch (e) {
        app.innerHTML = `
      <h1>Rentals for Court</h1>
      <p style="color:red;">Could not load rentals.</p>
      <a href="/clubs/${clubID}/courts/${courtID}" data-link class="btn btn-outline-secondary btn-sm me-2 mt-3">← Back to Court</a>
      <a href="/" data-link class="btn btn-outline-secondary btn-sm mt-3">Home</a>
    `;
    }
}




export async function rentalDetailsHandler(app, clubID, courtID, rentalID) {
    app.innerHTML = `<h1>Loading rental details...</h1>`;

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
            <tr><th scope="row">Rental ID</th><td>${rental.rentalID}</td></tr>
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

        // Tablo satırlarını oluştur
        const tableRows = rentalsWithNames.map(rental => `
      <tr>
        <td>${rental.rentalID}</td>
        <td>${rental.clubName}</td>
        <td>${rental.courtName}</td>
        <td>${rental.startTime}</td>
        <td>${rental.duration}</td>
        <td><a href="/clubs/${rental.clubId}/courts/${rental.courtId}/rentals/${rental.rentalID}" data-link>View</a></td>
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
