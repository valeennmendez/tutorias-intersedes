import { useState } from "react";
import { Link } from "react-router-dom";
import {
  CheckCircle,
  Trash2,
  Users,
  Home,
  BookOpen,
  ClipboardList,
  GraduationCap,
} from "lucide-react";
import heroImage from "../assets/hero.png";

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

import { Button } from "@/components/ui/button";

const EMAIL_EJEMPLO = "usuario.ejemplo@gmail.com";

function obtenerEmailDesdeToken(token) {
  if (!token) return EMAIL_EJEMPLO;

  const cleanToken = token.replace("Bearer ", "");
  const payload = cleanToken.split(".")[1];

  if (!payload) return EMAIL_EJEMPLO;

  try {
    const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = atob(base64);
    const decoded = JSON.parse(jsonPayload);

    return decoded.sub || EMAIL_EJEMPLO;
  } catch {
    return EMAIL_EJEMPLO;
  }
}

const TUTORES_INICIALES = [
  {
    id: 1,
    apellido: "Pérez",
    nombre: "Juan",
    estado: "pendiente",
  },
  {
    id: 2,
    apellido: "Gómez",
    nombre: "Ana",
    estado: "pendiente",
  },
  {
    id: 3,
    apellido: "López",
    nombre: "Martín",
    estado: "pendiente",
  },
];

export default function ValidacionTutorPage() {
  const [tutores, setTutores] = useState(TUTORES_INICIALES);
  const emailLogeado = obtenerEmailDesdeToken(localStorage.getItem("token"));

  const aceptarTutor = (id) => {
    setTutores((currentTutores) =>
      currentTutores.map((tutor) =>
        tutor.id === id
          ? { ...tutor, estado: "aceptado" }
          : tutor
      )
    );
  };

  const eliminarTutor = (id) => {
    setTutores((currentTutores) =>
      currentTutores.filter((tutor) => tutor.id !== id)
    );
  };

  return (
    <div className="min-h-screen bg-[#F7F9FB]">
      {/* Navbar */}
      <header className="border-b border-slate-200 bg-[#F7F9FB]">
        <div className="mx-auto flex h-20 max-w-7xl items-center justify-between px-6">
          {/* Logo */}
          <div className="flex items-center gap-4">
            <img src="/logo-unnoba.png" alt="logo-unnoba"  className="rounded-xl w-20" />

            <h1 className="text-2xl font-bold text-slate-900">
              Tutorías
            </h1>
          </div>

          {/* Menú */}
          <nav className="flex items-center gap-2">
            <Link
              to="/"
              className="flex items-center gap-2 rounded-xl px-4 py-2 text-slate-700 hover:bg-slate-200"
            >
              <Home className="h-5 w-5" />
              Inicio
            </Link>

            <Link
              to="/tutorias"
              className="flex items-center gap-2 rounded-xl px-4 py-2 text-slate-700 hover:bg-slate-200"
            >
              <BookOpen className="h-5 w-5" />
              Tutorías
            </Link>

            <Link
              to="/mis-inscripciones"
              className="flex items-center gap-2 rounded-xl px-4 py-2 text-slate-700 hover:bg-slate-200"
            >
              <ClipboardList className="h-5 w-5" />
              Mis Inscripciones
            </Link>

            <Link
              to="/validacion-tutores"
              className="flex items-center gap-2 rounded-xl bg-slate-200 px-4 py-2 text-slate-900"
            >
              <GraduationCap className="h-5 w-5" />
              Panel Tutor
            </Link>
          </nav>

          {/* Usuario */}
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-[#008BBA] font-semibold text-white">
              {emailLogeado.slice(0, 2).toUpperCase()}
            </div>

            <span className="font-medium text-slate-700">
              {emailLogeado}
            </span>
          </div>
        </div>
      </header>

      {/* Contenido */}
      <div className="px-4 py-10">
        <div className="mx-auto flex w-full max-w-5xl flex-col gap-6">
          <div className="flex flex-col gap-2 text-center sm:text-left">
            <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-sky-100 sm:mx-0">
              <Users className="h-6 w-6 text-[#008BBA]" />
            </div>

            <h1 className="text-3xl font-bold text-slate-900">
              Validación de Tutores
            </h1>

            <p className="text-sm text-slate-600">
              Revisá los postulantes y aceptá o eliminá cada tutor desde esta lista.
            </p>
          </div>

          <div className="grid gap-4">
            {tutores.map((tutor) => (
              <Card
                key={tutor.id}
                className="border-slate-200 shadow-md"
              >
                <CardHeader className="pb-3">
                  <div className="flex flex-col gap-1 sm:flex-row sm:items-center sm:justify-between">
                    <div>
                      <CardTitle className="text-xl text-slate-900">
                        {tutor.apellido}, {tutor.nombre}
                      </CardTitle>

                      <CardDescription>
                        Postulación de tutor pendiente de revisión
                      </CardDescription>
                    </div>

                    <span
                      className={`rounded-full px-3 py-1 text-xs font-semibold ${
                        tutor.estado === "aceptado"
                          ? "bg-emerald-100 text-emerald-700"
                          : "bg-amber-100 text-amber-700"
                      }`}
                    >
                      {tutor.estado === "aceptado"
                        ? "Aceptado"
                        : "Pendiente"}
                    </span>
                  </div>
                </CardHeader>

                <CardContent className="flex flex-col gap-4 pt-0 sm:flex-row sm:items-center sm:justify-end">
                  <Button
                    type="button"
                    onClick={() => aceptarTutor(tutor.id)}
                    disabled={tutor.estado === "aceptado"}
                    className="bg-[#008BBA] text-white hover:bg-[#00779f]"
                  >
                    <CheckCircle className="mr-2 h-4 w-4" />
                    Aceptar
                  </Button>

                  <Button
                    type="button"
                    variant="destructive"
                    onClick={() => eliminarTutor(tutor.id)}
                  >
                    <Trash2 className="mr-2 h-4 w-4" />
                    Eliminar
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}