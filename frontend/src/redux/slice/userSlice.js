import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  user: {
    username:'',
    location:'',
    isFirstLogin: '',
    role: '',
    accessToken: '',
  },
  isAuthenticated: false,
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
    }
  },
});

export const {  fetchUserLoginSuccess } = userSlice.actions;

export default userSlice.reducer;
