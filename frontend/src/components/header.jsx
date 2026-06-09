"use client";

import { Link, useNavigate, useLocation } from "react-router-dom";
import { useState } from "react";
import logo from "../assets/img/unnoba-logo.png";
import { Button, buttonVariants } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
  Home,
  BookOpen,
  Calendar,
  User,
  LogOut,
  Menu,
  GraduationCap,
  Shield,
  ClipboardList
} from "lucide-react";
import { cn } from "@/lib/utils";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";

export function DashboardHeader({ profile }) {
  const navigate = useNavigate();
  const location = useLocation();
  const pathname = location.pathname;
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = async () => {
    navigate("/");
  };

  const getInitials = (nombre, apellido) => {
    return `${nombre.charAt(0)}${apellido.charAt(0)}`.toUpperCase();
  };

  const getRoleLabel = (role) => {
    switch (role) {
      case "admin":
        return "Administrador";
      case "tutor":
        return "Tutor";
      default:
        return "Alumno";
    }
  };

  const navItems = [
    { href: "/dashboard", label: "Inicio", icon: Home },
    { href: "/dashboard/tutorias", label: "Tutorías", icon: BookOpen },
    { href: "/dashboard/mis-inscripciones", label: "Mis Inscripciones", icon: Calendar },
  ];

  if (profile.role === "tutor" || profile.role === "admin") {
    navItems.push({ href: "/tutor", label: "Panel Tutor", icon: GraduationCap });
  }

  if (profile.role === "admin") {
    navItems.push({ href: "/admin", label: "Administración", icon: Shield });
  }

  if (profile.role === "alumno") {
    navItems.push({ href: "/dashboard/postulacion-tutor", label: "Ser Tutor", icon: ClipboardList });
  }

  return (
    <header className="sticky top-0 z-50 border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container mx-auto flex h-16 items-center justify-between px-4">
        <Link to="/dashboard" className="flex items-center gap-3">
          <img
            src={logo}
            alt="UNNOBA Logo"
            width={120}
            height={60}
            className="h-10 w-auto"
          />
          <span className="hidden font-semibold lg:inline-block text-foreground">
            Tutorías
          </span>
        </Link>

        <nav className="hidden md:flex items-center gap-1">
          {navItems.map((item) => {
            const isActive = pathname === item.href;
            return (
              <Link
                key={item.href}
                to={item.href}
                className={cn(
                  buttonVariants({ variant: isActive ? "secondary" : "ghost", size: "sm" }),
                  "inline-flex items-center gap-2",
                  isActive
                    ? "bg-primary/10 text-primary hover:bg-primary/15"
                    : "hover:bg-sky-100 hover:text-sky-900"
                )}
              >
                <item.icon className="h-4 w-4" />
                {item.label}
              </Link>
            );
          })}
        </nav>

        <div className="flex items-center gap-2">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="gap-2 px-2">
                <Avatar className="h-8 w-8">
                  <AvatarFallback className="bg-primary text-primary-foreground text-sm">
                    {getInitials(profile.nombre, profile.apellido)}
                  </AvatarFallback>
                </Avatar>
                <span className="hidden sm:inline-block text-sm font-medium">
                  {profile.nombre}
                </span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-56">
              <DropdownMenuLabel>
                <div className="flex flex-col space-y-1">
                  <p className="text-sm font-medium">{profile.nombre} {profile.apellido}</p>
                  <p className="text-xs text-muted-foreground">{profile.email}</p>
                  <span className="inline-flex items-center rounded-full bg-primary/10 px-2 py-0.5 text-xs font-medium text-primary w-fit mt-1">
                    {getRoleLabel(profile.role)}
                  </span>
                </div>
              </DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem asChild>
                <Link to="/dashboard/perfil" className="cursor-pointer">
                  <User className="mr-2 h-4 w-4" />
                  Mi Perfil
                </Link>
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={handleLogout} className="text-destructive cursor-pointer">
                <LogOut className="mr-2 h-4 w-4" />
                Cerrar Sesión
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>

          <Sheet open={mobileMenuOpen} onOpenChange={setMobileMenuOpen}>
            <SheetTrigger asChild className="md:hidden">
              <Button variant="ghost" size="icon">
                <Menu className="h-5 w-5" />
                <span className="sr-only">Abrir menú</span>
              </Button>
            </SheetTrigger>
            <SheetContent side="right" className="w-64">
              <nav className="flex flex-col gap-2 mt-8">
                {navItems.map((item) => {
                  const isActive = pathname === item.href;
                  return (
                    <Link
                      key={item.href}
                      to={item.href}
                      onClick={() => setMobileMenuOpen(false)}
                      className={cn(
                        buttonVariants({ variant: isActive ? "secondary" : "ghost" }),
                        "justify-start inline-flex items-center gap-3",
                        isActive
                          ? "bg-primary/10 text-primary"
                          : "hover:bg-sky-100 hover:text-sky-900"
                      )}
                    >
                      <item.icon className="h-4 w-4" />
                      {item.label}
                    </Link>
                  );
                })}
              </nav>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </header>
  );
}
