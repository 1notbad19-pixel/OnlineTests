import axios from 'axios';
import { Quiz, QuizRequest, Question, User } from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// === QUIZ API ===
export const quizApi = {
  getAll: (params?: { category?: string; published?: boolean; tag?: string }) =>
    api.get<Quiz[]>('/quizzes', { params }),

  getById: (id: number) => api.get<Quiz>(`/quizzes/${id}`),

  getWithDetails: (id: number) => api.get<Quiz>(`/quizzes/${id}/details`),

  create: (data: QuizRequest) => api.post<Quiz>('/quizzes', data),

  update: (id: number, data: QuizRequest) => api.put<Quiz>(`/quizzes/${id}`, data),

  delete: (id: number) => api.delete(`/quizzes/${id}`),

  getFilters: (page: number = 0, size: number = 10, category?: string, published?: boolean, minQuestions?: number) =>
    api.get<{ content: Quiz[]; totalPages: number; totalElements: number }>('/quizzes/filter', {
      params: { page, size, category, published, minQuestions }
    }),
};

// === QUESTION API ===
export const questionApi = {
  getByQuizId: (quizId: number) => api.get<Question[]>(`/questions/quiz/${quizId}`),

  create: (data: { text: string; type: string; points: number; quizId: number; answers?: any[] }) =>
    api.post<Question>('/questions', data),

  update: (id: number, data: any) => api.put<Question>(`/questions/${id}`, data),

  delete: (id: number) => api.delete(`/questions/${id}`),
};

// === USER API ===
export const userApi = {
  getAll: () => api.get<User[]>('/users'),
  getById: (id: number) => api.get<User>(`/users/${id}`),
  getByUsername: (username: string) => api.get<User>(`/users/username/${username}`),
  create: (data: { username: string; email: string; password: string; firstName?: string; lastName?: string }) =>
    api.post<User>('/users', data),
  update: (id: number, data: any) => api.put<User>(`/users/${id}`, data),
  delete: (id: number) => api.delete(`/users/${id}`),
};

// === TAG API ===
export const tagApi = {
  getAll: () => api.get<{ id: number; name: string }[]>('/tags'),
  getByName: (name: string) => api.get<{ id: number; name: string }>(`/tags/name/${name}`),
  create: (name: string) => api.post<{ id: number; name: string }>('/tags', { name }),
  delete: (id: number) => api.delete(`/tags/${id}`),
};

export default api;