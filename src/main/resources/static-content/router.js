import {
    homeHandler,
    clubsListHandler,
    clubDetailsHandler,
    courtsListHandler,
    courtDetailsHandler,
    courtRentalsListHandler,
    rentalDetailsHandler,
    usersListHandler,
    userDetailsHandler,
    userRentalsListHandler,
} from '/static-content/handler.js';

const routes = [
    { path: /^\/$/, handler: homeHandler },
    { path: /^\/clubs$/, handler: clubsListHandler },
    { path: /^\/clubs\/(\d+)$/, handler: clubDetailsHandler },
    { path: /^\/clubs\/(\d+)\/courts$/, handler: courtsListHandler },
    { path: /^\/clubs\/(\d+)\/courts\/(\d+)$/, handler: courtDetailsHandler },
    { path: /^\/clubs\/(\d+)\/courts\/(\d+)\/rentals$/, handler: courtRentalsListHandler },
    { path: /^\/clubs\/(\d+)\/courts\/(\d+)\/rentals\/(\d+)$/, handler: rentalDetailsHandler },
    { path: /^\/users$/, handler: usersListHandler },
    { path: /^\/users\/(\d+)$/, handler: userDetailsHandler },
    { path: /^\/users\/(\d+)\/rentals$/, handler: userRentalsListHandler },
    { path: /^\/users\/(\d+)\/rentals\/(\d+)$/, handler: rentalDetailsHandler }
];

export async function router() {
    const path = window.location.pathname;
    const app = document.getElementById('app');
    for (const route of routes) {
        const match = path.match(route.path);
        if (match) {
            await route.handler(app, ...match.slice(1));
            return;
        }
    }
    app.innerHTML = `<h1>404 - Page Not Found</h1>
    <nav>
      <a href="/" data-link>Home</a>
    </nav>
  `;
}
