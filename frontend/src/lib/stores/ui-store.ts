import { create } from 'zustand'

type UiState = {
  darkMode: boolean
  sidebarOpen: boolean
  setDarkMode: (value: boolean) => void
  setSidebarOpen: (value: boolean) => void
}

export const useUiStore = create<UiState>((set) => ({
  darkMode: true,
  sidebarOpen: true,
  setDarkMode: (darkMode) => set({ darkMode }),
  setSidebarOpen: (sidebarOpen) => set({ sidebarOpen })
}))

