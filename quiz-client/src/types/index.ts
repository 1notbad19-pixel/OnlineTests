export interface Quiz {
  id: number;
  title: string;
  description: string;
  category: string;
  timeLimitMinutes: number;
  maxAttempts: number;
  isPublished: boolean;
  passingScore: number;
  createdAt: string;
  updatedAt: string;
  tags: string[];
  questionCount: number;
}

export interface QuizRequest {
  title: string;
  description?: string;
  category: string;
  timeLimitMinutes: number;
  maxAttempts?: number;
  isPublished?: boolean;
  passingScore?: number;
  tags?: string[];
}

export interface Question {
  id: number;
  text: string;
  type: string;
  points: number;
  answers: Answer[];
}

export interface Answer {
  id: number;
  text: string;
  isCorrect: boolean;
}

export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  createdAt: string;
  quizzesCount: number;
}