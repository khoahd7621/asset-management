import { BrowserRouter, Route, Routes } from 'react-router-dom';

import Admin from './layouts/Admin';
import User from './layouts/User';
import { CreateUser, Home as AdminHome } from './pages/admin';
import { Home as UserHome } from './pages/user';
import { adminRoute, userRoute } from './routes/routes';

const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path={adminRoute.home} element={<Admin />}>
          <Route index element={<AdminHome />} />
          {/* Add more route here - like format below ... */}
          {/* <Route path={adminRoute.[your-path]} element={[your-component]} /> */}
          <Route path={`${adminRoute.manageUser}/${adminRoute.createUser}`} element={<CreateUser />} />
        </Route>
        <Route path={userRoute.home} element={<User />}>
          <Route index element={<UserHome />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default App;
