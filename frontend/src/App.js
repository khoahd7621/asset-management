import { BrowserRouter, Route, Routes } from 'react-router-dom';

import Admin from './layouts/Admin';
import Login from './layouts/Login';
import User from './layouts/User';
import { CreateUser, Home as AdminHome, ManageUser } from './pages/admin';
import { Home as UserHome } from './pages/user';
import AdminPrivateRoute from './routes/AdminPrivateRoute';
import { adminRoute, userRoute } from './routes/routes';
import StaffPrivateRoute from './routes/StaffPrivateRoute';

const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route
          path={adminRoute.home}
          element={
            <AdminPrivateRoute>
              <Admin />
            </AdminPrivateRoute>
          }
        >
          <Route index element={<AdminHome />} />
          {/* Add more route here - like format below ... */}
          {/* <Route path={adminRoute.[your-path]} element={[your-component]} /> */}
          <Route path={adminRoute.manageUser} element={<ManageUser />} />
          <Route path={`${adminRoute.manageUser}/${adminRoute.createUser}`} element={<CreateUser />} />
        </Route>
        <Route
          path={userRoute.home}
          element={
            <StaffPrivateRoute>
              <User />
            </StaffPrivateRoute>
          }
        >
          <Route index element={<UserHome />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default App;
