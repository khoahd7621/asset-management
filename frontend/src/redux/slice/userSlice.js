import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  user: {
    username: '',
    location: '',
    isFirstLogin: '',
    role: '',
    accessToken: '',
  },
  isAuthenticated: false,
  isFirstLogin: true,
};

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    fetchUserLoginSuccess: (state, action) => {
      state.user = {
        ...action.payload,
      };
      state.isAuthenticated = true;
      state.isFirstLogin = action.payload.isFirstLogin;
    },
    updateFirstLogin: (state) => {
      state.user = {
        ...state.user,
        isFirstLogin: '',
      };
      state.isFirstLogin = false;
    },
    removeDataUserLogout: (state) => {
      state.user = {
        ...initialState.user,
      };
      state.isAuthenticated = initialState.isAuthenticated;
      state.isFirstLogin = initialState.isFirstLogin;
    },
  },
});

export const { fetchUserLoginSuccess, removeDataUserLogout, updateFirstLogin } = userSlice.actions;

export default userSlice.reducer;
