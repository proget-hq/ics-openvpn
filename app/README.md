# Proget OpenVpn

## How to update OpenVpn with main project

- get list of all not updated commits from main project
- cherry pick (not rebase) them `git cherry-pick <startCommit>..<endCommit> / <old>..<new>`, then:
    - resolve conflicts
    - `git cherry-pick --continue`
    - `git commit --allow-empty` (if needed)

  (repeat this steps until all commits are picked)
- update submodules (ignore changes in submodules from cherry-pick):
    - `git submodule sync`
    - `git submodule update `
      // `git submodule status` / `git diff` 

  (on main project should show no changes in submodules)
- build project and check if everything works (fix problems if needed)
- push project to external github repository (https://github.com/proget-hq/ics-openvpn), to ensure the terms of the GPL license

Congrats, you have successfully update OpenVpn project!
