import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useNavigate } from 'react-router'
import { Mail } from 'lucide-react'
import AuthLayout from '../components/AuthLayout'
import CampoFormulario from '../components/CampoFormulario'
import CampoSenha from '../components/CampoSenha'
import AlertaDeErro from '../components/AlertaDeErro'
import BotaoEnviar from '../components/BotaoEnviar'
import { classeInput } from '../components/estilos'
import { useAuth } from '../contexts/useAuth'
import { mensagemDeErro } from '../services/erros'

const schema = z.object({
  email: z.string().min(1, 'E-mail é obrigatório').email('E-mail inválido'),
  senha: z.string().min(1, 'Senha é obrigatória'),
})

type DadosDoLogin = z.infer<typeof schema>

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [erroDoServidor, setErroDoServidor] = useState('')

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<DadosDoLogin>({ resolver: zodResolver(schema) })

  async function aoEnviar(dados: DadosDoLogin) {
    setErroDoServidor('')
    try {
      await login(dados.email, dados.senha)
      navigate('/dashboard', { replace: true })
    } catch (erro) {
      setErroDoServidor(mensagemDeErro(erro))
    }
  }

  return (
    <AuthLayout>
      {erroDoServidor && <AlertaDeErro mensagem={erroDoServidor} />}

      <form onSubmit={handleSubmit(aoEnviar)} noValidate className="space-y-4">
        <CampoFormulario id="email" rotulo="E-mail" icone={Mail} erro={errors.email?.message}>
          <input
            id="email"
            type="email"
            autoComplete="email"
            placeholder="seu.email@exemplo.com"
            className={classeInput}
            {...register('email')}
          />
        </CampoFormulario>

        <CampoSenha
          registro={register('senha')}
          erro={errors.senha?.message}
          autoComplete="current-password"
        />

        <BotaoEnviar carregando={isSubmitting} texto="Acessar Plataforma" />
      </form>
    </AuthLayout>
  )
}