package com.ts.saude.dto;

public class SlotDTO {
    private String hora;
    private boolean confirmado;
    private String pacienteNome;
    private String especialidade;
    private Long id;

    public SlotDTO(String hora, boolean confirmado, String pacienteNome, String especialidade, Long id) {
        this.hora = hora;
        this.confirmado = confirmado;
        this.pacienteNome = pacienteNome;
        this.especialidade = especialidade;
        this.id = id;
    }

    public String getHora() {
        return hora;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public String getPacienteNome() {
        return pacienteNome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public Long getId() {
        return id;
    }
}
